package com.example.alex.gismasterappmvp.retrofit;

import com.example.alex.gismasterappmvp.mvp.models.CityInfo;
import com.example.alex.gismasterappmvp.mvp.models.WeatherCurrentInfo;
import com.example.alex.gismasterappmvp.mvp.models.WeatherInfo;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface GismasterApi {
    /**
     * Получает прогноз погоды в виде массива.
     *
     * @param post тело POST запроса
     * @return массив прогноза погоды
     */
    @POST("/five")
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    Single<WeatherInfo[]> getForecast(@Body CityInfo post);

    /**
     * Получает текущую погоду.
     *
     * @param post тело POST запроса
     * @return текущую погоду как обьект {@link WeatherCurrentInfo}
     */
    @POST("/current")
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    Single<WeatherCurrentInfo> getCurrentWeather(@Body CityInfo post);

    /**
     * Получает информацию о месте (координаты, название города (местности), страны, ID).
     *
     * @param post тело POST запроса
     * @return информацию о месте как обьект {@link CityInfo}
     */
    @POST("/location")
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    Single<CityInfo> getLocation(@Body CityInfo post);
}
