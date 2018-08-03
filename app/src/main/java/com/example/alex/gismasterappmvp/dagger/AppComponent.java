package com.example.alex.gismasterappmvp.dagger;

import android.content.Context;

import com.example.alex.gismasterappmvp.dagger.modules.ContextModule;
import com.example.alex.gismasterappmvp.dagger.modules.GismasterModule;
import com.example.alex.gismasterappmvp.mvp.presenters.ForecastDetailPresenter;
import com.example.alex.gismasterappmvp.mvp.presenters.HistoryPresenter;
import com.example.alex.gismasterappmvp.retrofit.GismasterService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ContextModule.class, GismasterModule.class})
public interface AppComponent {

    Context getContext();
    GismasterService getGismasterService();

    void inject(HistoryPresenter historyPresenter);
    void inject(ForecastDetailPresenter forecastDetailPresenter);
}
