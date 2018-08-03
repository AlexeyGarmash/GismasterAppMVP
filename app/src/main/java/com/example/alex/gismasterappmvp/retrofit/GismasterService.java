package com.example.alex.gismasterappmvp.retrofit;

import com.example.alex.gismasterappmvp.mvp.models.CityInfo;
import com.example.alex.gismasterappmvp.mvp.models.WeatherCurrentInfo;
import com.example.alex.gismasterappmvp.mvp.models.WeatherInfo;

import io.reactivex.Single;


public class GismasterService {

    private GismasterApi mGismasterApi;

    public GismasterService(GismasterApi gismasterApi){
        mGismasterApi = gismasterApi;
    }

    public Single<WeatherInfo[]> getForecast(CityInfo post){
        return  mGismasterApi.getForecast(post);
    }

    public Single<WeatherCurrentInfo> getCurrentWeather(CityInfo post){
        return mGismasterApi.getCurrentWeather(post);
    }

    public Single<CityInfo> getLocation(CityInfo post){
        return mGismasterApi.getLocation(post);
    }
}
