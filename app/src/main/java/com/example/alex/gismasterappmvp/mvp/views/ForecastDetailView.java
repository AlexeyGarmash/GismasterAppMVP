package com.example.alex.gismasterappmvp.mvp.views;

import android.content.Intent;

import com.arellomobile.mvp.viewstate.strategy.AddToEndStrategy;
import com.arellomobile.mvp.viewstate.strategy.SingleStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.example.alex.gismasterappmvp.mvp.models.WeatherCurrentInfo;
import com.example.alex.gismasterappmvp.mvp.models.WeatherInfo;

import java.util.List;

@StateStrategyType(AddToEndStrategy.class)
public interface ForecastDetailView extends BaseLceView {

    void clearData();

    void setupForecastData(List<WeatherInfo> weatherInfos);

    void setupWeatherData(WeatherCurrentInfo currentInfo);

    void goToMap(Intent intent);
}
