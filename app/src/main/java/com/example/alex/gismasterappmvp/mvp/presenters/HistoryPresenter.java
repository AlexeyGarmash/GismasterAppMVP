package com.example.alex.gismasterappmvp.mvp.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.example.alex.gismasterappmvp.base.BaseApplication;
import com.example.alex.gismasterappmvp.common.Constants;
import com.example.alex.gismasterappmvp.mvp.models.CityInfo;
import com.example.alex.gismasterappmvp.mvp.models.CityInfoRealm;
import com.example.alex.gismasterappmvp.mvp.models.WeatherCurrentInfo;
import com.example.alex.gismasterappmvp.mvp.views.HistoryView;
import com.example.alex.gismasterappmvp.realm.RealmDb;
import com.example.alex.gismasterappmvp.retrofit.GismasterService;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

@InjectViewState
public class HistoryPresenter extends BasePresenter<HistoryView> {
    @Inject
    GismasterService mGismasterService;


    Realm realm;
    RealmResults<CityInfoRealm> results;
    RealmChangeListener<RealmResults<CityInfoRealm>> resultsRealmChangeListener = new RealmChangeListener<RealmResults<CityInfoRealm>>() {
        @Override
        public void onChange(RealmResults<CityInfoRealm> cityInfoRealms) {
            if (cityInfoRealms.isLoaded()) {
                updateUi(cityInfoRealms, true);
            }
        }
    };

    private void updateUi(List<CityInfoRealm> cityInfoRealms, boolean isRefreshing) {

        try {
            for (CityInfoRealm city : cityInfoRealms) {
                sendPostCurrentWeather(city.getCityInfo(), Constants.DATABASE_DOWNLOAD, 0, isRefreshing);
            }
        } finally {
            if (realm != null) {
                results.removeChangeListener(resultsRealmChangeListener);
                results = null;
                realm.close();
            }
        }
    }

    private boolean mIsLoading;

    public HistoryPresenter() {
        BaseApplication.getsAppComponent().inject(this);
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        loadDatabase();
    }

    public void addCurrentCityInfo(String addr, boolean isRefreshing) {
        //addData(cityInfo, isRefreshing);
        sendPostLocation(addr, isRefreshing);
    }

    public void loadDatabase() {
        getViewState().clearData();
        getViewState().onStartLoading();
        showProgress(true);
        realm = Realm.getDefaultInstance();
        results = realm.where(CityInfoRealm.class)
                .findAllAsync();
        results.addChangeListener(resultsRealmChangeListener);
    }


    /**
     * Метод, посылающий запрос на сервер для получения
     * координат по полученному адресу.
     *
     * @param addr точный адрес введенного пользователем места
     */
    private void sendPostLocation(String addr, final boolean isRefreshing) {
        getViewState().onStartLoading();
        showProgress(isRefreshing);
        unsubscribeOnDestroy(mGismasterService.getLocation(new CityInfo(addr))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<CityInfo>() {

                    @Override
                    public void onSuccess(CityInfo cityInfo) {
                        sendPostCurrentWeather(cityInfo, Constants.ADD_TO_LIST, 0, isRefreshing);
                        RealmDb.insertRealmModel(cityInfo.getCityInfoRealm());
                        //mProgressBarStatus.setVisibility(View.INVISIBLE);
                        //stopProgress();
                    }

                    @Override
                    public void onError(Throwable e) {
                        onLoadingFinish(isRefreshing);
                        onLoadingFailed(e);
                        //mProgressBarStatus.setVisibility(View.INVISIBLE);
                        //stopProgress();
                    }
                }));
    }


    /**
     * Посылает запрос на сервер для получения текущей (на момент API)
     * статуса погоды на местности.
     *
     * @param cityInfo содержит координаты, нужные для получения погоды
     * @param type     тип обращения к методу: UPDATE_WEATHER - обновить данные списка,
     *                 ADD_TO_LIST - добавить в список,
     *                 DATABASE_DOWNLOAD - загрузить с БД
     * @param index    индекс города для обновления в списке
     */
    private void sendPostCurrentWeather(final CityInfo cityInfo, final int type, final int index, final boolean isRefreshing) {
        unsubscribeOnDestroy(mGismasterService.getCurrentWeather(cityInfo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<WeatherCurrentInfo>() {

                    @Override
                    public void onSuccess(WeatherCurrentInfo currentWeatherInfo) {
                        onLoadingFinish(isRefreshing);
                        onLoadingSuccess(currentWeatherInfo, cityInfo, type, index);
                    }

                    @Override
                    public void onError(Throwable e) {
                        onLoadingFinish(isRefreshing);
                        onLoadingFailed(e);
                    }
                }));
    }

    private void onLoadingFailed(Throwable e) {
        getViewState().showError(e.getMessage());
    }

    private void onLoadingSuccess(WeatherCurrentInfo currentWeatherInfo, CityInfo cityInfo, int type, int index) {
        switch (type) {
            case Constants.UPDATE_WEATHER:
                getViewState().updateCurrentWeather(currentWeatherInfo, index);
                break;
            case Constants.ADD_TO_LIST:
                getViewState().addToList(currentWeatherInfo, cityInfo);
                break;
            case Constants.DATABASE_DOWNLOAD:
                getViewState().addFromDatabase(currentWeatherInfo, cityInfo);
                break;
        }
    }


    private void onLoadingFinish(boolean isRefreshing) {
        getViewState().onFinishLoading();
        hideProgress(isRefreshing);
    }

    private void hideProgress(boolean isRefreshing) {
        if (isRefreshing) {
            getViewState().hideRefreshing();
        } else {
            getViewState().hideListProgress();
        }
    }


    private void showProgress(boolean isRefreshing) {
        if (isRefreshing) {
            getViewState().showRefreshing();
        } else {
            getViewState().showListProgress();
        }
    }
}
