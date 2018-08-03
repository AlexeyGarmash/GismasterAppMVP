package com.example.alex.gismasterappmvp.activities;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.example.alex.gismasterappmvp.R;
import com.example.alex.gismasterappmvp.Utils;
import com.example.alex.gismasterappmvp.adapters.HistoryAdapter;
import com.example.alex.gismasterappmvp.mvp.models.CityInfo;
import com.example.alex.gismasterappmvp.mvp.models.WeatherCurrentInfo;
import com.example.alex.gismasterappmvp.mvp.presenters.HistoryPresenter;
import com.example.alex.gismasterappmvp.mvp.views.HistoryView;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.willowtreeapps.spruce.Spruce;
import com.willowtreeapps.spruce.animation.DefaultAnimations;
import com.willowtreeapps.spruce.sort.DefaultSort;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInLeftAnimationAdapter;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class MainActivity extends MvpAppCompatActivity implements HistoryView {


    private static final String TAG = "MainActivity";
    public static String LAT_LON_ADDRESS_DATA;
    @InjectPresenter
    HistoryPresenter mHistoryPresenter;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.rvHistory)
    RecyclerView mRecyclerView;
    @BindView(R.id.tvListEmpty)
    TextView mTextViewEmptyList;
    @BindView(R.id.swipeLayoutMain)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;

    private HistoryAdapter mHistoryAdapter;
    private Animator spruceAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mHistoryAdapter = new HistoryAdapter(new ArrayList<WeatherCurrentInfo>(), this);
        mRecyclerView.setAdapter(mHistoryAdapter);
        setAutoCompleteFragment();
        setRefreshLayout();
        mHistoryAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (mHistoryAdapter.getItemCount() == 0) {
                    mTextViewEmptyList.setVisibility(View.VISIBLE);
                } else {
                    mTextViewEmptyList.setVisibility(View.INVISIBLE);
                }
            }
        });
        setupAnimator();

    }

    /**
     * Метод служит для инициализации и установки параметров
     * {@link SwipeRefreshLayout}, необходимого для обновления информации по свайпу сверху вниз.
     */
    private void setRefreshLayout() {
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mHistoryPresenter.loadDatabase();
            }
        });
    }

    private void setAutoCompleteFragment() {
        final PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setHint("Найти место");
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .build();
        autocompleteFragment.setFilter(typeFilter);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.

                Log.i(TAG, "Place: " + place.getName());//get place details here
                String address = place.getAddress().toString();

                mHistoryPresenter.addCurrentCityInfo(address, true);
                autocompleteFragment.setText("");
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);

            }
        });
    }

    public void setupAnimator() {
        SlideInLeftAnimator animator = new SlideInLeftAnimator(new OvershootInterpolator(1f));
        Utils.setRecyclerViewAnimator(mRecyclerView, animator, 700);
    }

    private void showSnackbar(String message) {
        Snackbar.make(mRecyclerView, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showError(String message) {
        showSnackbar(message);
    }

    @Override
    public void hideError() {

    }

    @Override
    public void onStartLoading() {
        mSwipeRefreshLayout.setEnabled(false);
    }

    @Override
    public void onFinishLoading() {
        mSwipeRefreshLayout.setEnabled(true);
    }

    @Override
    public void showRefreshing() {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
    }

    @Override
    public void hideRefreshing() {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void showListProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideListProgress() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void updateCurrentWeather(WeatherCurrentInfo weatherCurrentInfo, int index) {
        mHistoryAdapter.getCities().get(index).setNewCurrentWeather(weatherCurrentInfo);
        mHistoryAdapter.notifyItemChanged(index);
    }

    @Override
    public void addToList(WeatherCurrentInfo weatherCurrentInfo, CityInfo cityInfo) {
        mHistoryAdapter.addCity(weatherCurrentInfo, cityInfo);
    }

    @Override
    public void addFromDatabase(WeatherCurrentInfo weatherCurrentInfo, CityInfo cityInfo) {
        weatherCurrentInfo.getCoord().setCityName(cityInfo.getCityName());
        weatherCurrentInfo.getCoord().setCountryName(cityInfo.getCountryName());
        weatherCurrentInfo.getCoord().setId(cityInfo.getId());
        mHistoryAdapter.getCities().add(weatherCurrentInfo);
        mHistoryAdapter.notifyDataSetChanged();
    }

    @Override
    public void clearData() {
        mHistoryAdapter.getCities().clear();
        mHistoryAdapter.notifyDataSetChanged();
    }
}
