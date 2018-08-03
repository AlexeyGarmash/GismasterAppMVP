package com.example.alex.gismasterappmvp.mvp.presenters;


import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.arellomobile.mvp.InjectViewState;
import com.example.alex.gismasterappmvp.activities.ForecastActivity;
import com.example.alex.gismasterappmvp.activities.MainActivity;
import com.example.alex.gismasterappmvp.activities.MapsActivity;
import com.example.alex.gismasterappmvp.base.BaseApplication;
import com.example.alex.gismasterappmvp.common.Constants;
import com.example.alex.gismasterappmvp.mvp.models.CityInfo;
import com.example.alex.gismasterappmvp.mvp.models.WeatherCurrentInfo;
import com.example.alex.gismasterappmvp.mvp.models.WeatherInfo;
import com.example.alex.gismasterappmvp.mvp.views.ForecastDetailView;
import com.example.alex.gismasterappmvp.retrofit.GismasterService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

@InjectViewState
public class ForecastDetailPresenter extends BasePresenter<ForecastDetailView> {
    @Inject
    GismasterService mGismasterService;


    private WeatherCurrentInfo mWeatherCurrentInfo;

    private ArrayList<String> daysDescriptionShort;

    public ForecastDetailPresenter() {

    }

    public ForecastDetailPresenter(WeatherCurrentInfo weatherCurrentInfo) {
        mWeatherCurrentInfo = weatherCurrentInfo;
        BaseApplication.getsAppComponent().inject(this);
        daysDescriptionShort = new ArrayList<>();
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        loadForecast();
    }

    public void updateInfo() {
        getViewState().clearData();
        loadForecast();
    }

    private void getDayDescriptions(Integer[] which){
        String[] days = new String[]{"Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"};
        ArrayList<String> daysResult = new ArrayList<>();
        for (Integer index : which) {
            daysResult.add(days[index]);
        }
        daysDescriptionShort = daysResult;
    }

    public void updateForecastByFilters(Integer[] which) {
        getViewState().clearData();
        getDayDescriptions(which);
        getForecast(mWeatherCurrentInfo.getCoord(), true, Constants.FILTER_FORECAST);
    }

    private void loadForecast() {
        getForecast(mWeatherCurrentInfo.getCoord(), true, Constants.UPDATE_FORECAST);
        getCurrentWeather(mWeatherCurrentInfo.getCoord(), true);
    }

    public void getForecast(CityInfo cityInfo, boolean isRefreshing, String type) {
        sendPostForecast(cityInfo, isRefreshing, type);
    }

    public void getCurrentWeather(CityInfo cityInfo, boolean isRefreshing) {
        sendPostCurrentWeather(cityInfo, isRefreshing);
    }

    public void goToMap(Intent intent) {
        intent.putExtra(MainActivity.LAT_LON_ADDRESS_DATA, mWeatherCurrentInfo);
        getViewState().goToMap(intent);
    }

    /**
     * Посылает запрос на сервер для получения прогноза
     * погоды на местности и заполняет список прогноза погоды.
     *
     * @param cityInfo содержит координаты, нужные для получения погоды
     */
    private void sendPostForecast(final CityInfo cityInfo, final boolean isRefreshing, final String type) {
        getViewState().onStartLoading();
        showProgress(isRefreshing);
        unsubscribeOnDestroy(mGismasterService.getForecast(cityInfo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<WeatherInfo[]>() {

                    @Override
                    public void onSuccess(WeatherInfo[] forecastInfo) {
                        onLoadingFinish(isRefreshing);
                        switch (type) {
                            case Constants.FILTER_FORECAST:
                                onLoadingSuccess(forecastInfo, daysDescriptionShort);
                                break;
                            case Constants.UPDATE_FORECAST:
                                onLoadingSuccess(forecastInfo);
                                break;
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        onLoadingFinish(isRefreshing);
                        onLoadingFailed(e);
                    }
                }));
    }

    /**
     * Посылает запрос на сервер для получения текущей (на момент API)
     * статуса погоды на местности и заполянет соответсвующими данными
     * окно текущей погоды.
     *
     * @param cityInfo содержит координаты, нужные для получения погоды
     */
    private void sendPostCurrentWeather(CityInfo cityInfo, final boolean isRefreshing) {
        getViewState().onStartLoading();
        showProgress(isRefreshing);
        unsubscribeOnDestroy(mGismasterService.getCurrentWeather(cityInfo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<WeatherCurrentInfo>() {

                    @Override
                    public void onSuccess(WeatherCurrentInfo response) {
                        onLoadingFinish(isRefreshing);
                        onLoadingSuccess(response);
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

    private void onLoadingSuccess(WeatherCurrentInfo currentInfo) {
        WeatherCurrentInfo response = currentInfo;
        response.getCoord().setCityName(mWeatherCurrentInfo.getCoord().getCityName());
        response.getCoord().setCountryName(mWeatherCurrentInfo.getCoord().getCountryName());
        getViewState().setupWeatherData(response);
        mWeatherCurrentInfo = response;
    }

    private void onLoadingSuccess(WeatherInfo[] forecastInfo) {
        getViewState().setupForecastData(getForecastFromArray(forecastInfo));
    }

    private void onLoadingSuccess(WeatherInfo[] forecastInfo, ArrayList<String> daysDescriptionShort) {
        getViewState().setupForecastData(getForecastFromArray(forecastInfo, daysDescriptionShort));
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

    /**
     * Преобразовует массив прогноза погоды в список, нужный для адаптера.
     *
     * @param weathersArr массив прогноза погоды, полученный на запрос о прогнозе погоды на 5 дней.
     */
    private List<WeatherInfo> getForecastFromArray(WeatherInfo[] weathersArr) {
        List<WeatherInfo> forecastInfo = new ArrayList<>();
        for (WeatherInfo wInfo : weathersArr) {
            forecastInfo.add(wInfo);
        }
        return forecastInfo;
    }

    private List<WeatherInfo> getForecastFromArray(WeatherInfo[] weathersArr, ArrayList<String> daysDescriptionShort) {
        List<WeatherInfo> forecastInfo = new ArrayList<>();
        for (String day : daysDescriptionShort) {
            for (WeatherInfo wInfo : weathersArr) {
                if (day.equals(wInfo.getDayWeek())) {
                    forecastInfo.add(wInfo);
                }
            }
        }
        return forecastInfo;
    }

}
