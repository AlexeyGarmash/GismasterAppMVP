package com.example.alex.gismasterappmvp.activities;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.PresenterType;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.arellomobile.mvp.presenter.ProvidePresenterTag;
import com.example.alex.gismasterappmvp.R;
import com.example.alex.gismasterappmvp.Utils;
import com.example.alex.gismasterappmvp.adapters.ForecastAdapter;
import com.example.alex.gismasterappmvp.mvp.models.WeatherCurrentInfo;
import com.example.alex.gismasterappmvp.mvp.models.WeatherInfo;
import com.example.alex.gismasterappmvp.mvp.presenters.ForecastDetailPresenter;
import com.example.alex.gismasterappmvp.mvp.views.ForecastDetailView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.animators.LandingAnimator;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

public class ForecastActivity extends MvpAppCompatActivity implements ForecastDetailView {

    @BindView(R.id.toolbarForecast)
    Toolbar mToolbar;
    @BindView(R.id.rvForecast)
    RecyclerView mRecyclerView;

    @BindView(R.id.tvCityName)
    TextView mTextViewCity;

    @BindView(R.id.tvSunRise)
    TextView mTextViewSunRise;

    @BindView(R.id.tvSunSet)
    TextView mTextViewSunSet;

    @BindView(R.id.tvTemp)
    TextView mTextViewTemp;

    @BindView(R.id.tvDescription)
    TextView mTextViewDescription;

    @BindView(R.id.wind_direction_icon_current)
    ImageView mImageViewWindDir;

    @BindView(R.id.ivWeatherIcon)
    ImageView mImageViewWeatherIcon;

    @BindView(R.id.tvWind_description_current)
    TextView mTextViewWindDir;

    @BindView(R.id.tvWind_speed_current)
    TextView mTextViewWindSpeed;

    @BindView(R.id.sliding_layout)
    SlidingUpPanelLayout mSlidingUpPanelLayout;

    @BindView(R.id.swipeLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.btnOpenMap)
    Button mButtonMap;

    private WeatherCurrentInfo mCurrentInfo;

    private MaterialDialog materialDialogFilter;

    @InjectPresenter
    ForecastDetailPresenter mForecastDetailPresenter;

    @ProvidePresenter
    ForecastDetailPresenter provideForecastDetailPresenter() {
        mCurrentInfo = getIntent().getExtras().getParcelable(MainActivity.LAT_LON_ADDRESS_DATA);
        return new ForecastDetailPresenter(mCurrentInfo);
    }


    private ForecastAdapter mForecastAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mForecastAdapter = new ForecastAdapter(new ArrayList<WeatherInfo>(), this);
        mRecyclerView.setAdapter(mForecastAdapter);
        setRecyclerViewAnimator();
        setButton();
        setRefreshLayout();
        setFilterDialog();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mToolbar.setTitle(String.format("%s, %s", mCurrentInfo.getCoord().getCityName(), mCurrentInfo.getCoord().getCountryName()));
    }

    private void setRefreshLayout() {
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mForecastDetailPresenter.updateInfo();
            }
        });
    }

    private void setButton() {
        mButtonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mForecastDetailPresenter.goToMap(new Intent(ForecastActivity.this, MapsActivity.class));
            }
        });
    }

    private void setRecyclerViewAnimator() {
        LandingAnimator animator = new LandingAnimator(new OvershootInterpolator(1f));
        Utils.setRecyclerViewAnimator(mRecyclerView, animator, 1500);
    }


    private void setFilterDialog() {
        materialDialogFilter = new MaterialDialog.Builder(this)
                .title(R.string.filter_title)
                .items(R.array.days)
                .itemsCallbackMultiChoice(new Integer[]{0, 1, 2, 3, 4, 5, 6}, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {

                        mForecastDetailPresenter.updateForecastByFilters(which);
                        return true;
                    }
                })
                .positiveText(R.string.choose)
                .negativeText(R.string.dismiss).build();
    }

    @Override
    public void clearData() {
        mForecastAdapter.setForecast(new ArrayList<WeatherInfo>());
    }

    @Override
    public void setupForecastData(List<WeatherInfo> weatherInfos) {
        mForecastAdapter.setForecast(weatherInfos);
    }

    @Override
    public void setupWeatherData(WeatherCurrentInfo currentInfo) {
        setTextData(mTextViewCity, currentInfo.getCoord().getCityName());
        setTextData(mTextViewSunRise, currentInfo.getSunriseTime());
        setTextData(mTextViewSunSet, currentInfo.getSunsetTime());
        setTextData(mTextViewTemp, String.valueOf(currentInfo.getWeatherPart().getTemprInt()));
        setTextData(mTextViewDescription, currentInfo.getWeatherPart().getDescrip());
        setTextData(mTextViewWindDir, currentInfo.getWeatherPart().getDirectWind());
        setTextData(mTextViewWindSpeed, String.valueOf(currentInfo.getWeatherPart().getSpeedWind()));
        Utils.setImageByResource(mImageViewWindDir, currentInfo.getWeatherPart().getWindResource(), 60, 60, this);
        Utils.setImageByURL(this, mImageViewWeatherIcon, 60, 60, currentInfo.getWeatherPart().getWeatherIconURL());
    }

    /**
     * Служит для установки текста в {@link TextView}.
     */
    private void setTextData(TextView textView, String data) {
        textView.setText(data);
    }

    @Override
    public void goToMap(Intent intent) {
        startActivity(intent);
    }

    @Override
    public void showError(String message) {
        showSnackbar(message);
    }

    private void showSnackbar(String message) {
        Snackbar.make(mRecyclerView, message, Snackbar.LENGTH_LONG).show();
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

    }

    @Override
    public void hideListProgress() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filter_weather_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_filter) {
            materialDialogFilter.show();
        }
        return true;
    }

}
