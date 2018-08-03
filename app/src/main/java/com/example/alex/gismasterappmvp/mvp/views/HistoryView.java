package com.example.alex.gismasterappmvp.mvp.views;


import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.AddToEndStrategy;
import com.arellomobile.mvp.viewstate.strategy.SingleStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.example.alex.gismasterappmvp.mvp.models.CityInfo;
import com.example.alex.gismasterappmvp.mvp.models.WeatherCurrentInfo;


public interface HistoryView extends BaseLceView {

    void updateCurrentWeather(WeatherCurrentInfo weatherCurrentInfo, int index);
    @StateStrategyType(AddToEndStrategy.class)
    void addToList(WeatherCurrentInfo weatherCurrentInfo, CityInfo cityInfo);
    @StateStrategyType(AddToEndStrategy.class)
    void addFromDatabase(WeatherCurrentInfo weatherCurrentInfo, CityInfo cityInfo);
    @StateStrategyType(SingleStateStrategy.class)
    void clearData();
}
