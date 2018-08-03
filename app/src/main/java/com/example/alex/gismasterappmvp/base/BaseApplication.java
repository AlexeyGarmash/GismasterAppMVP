package com.example.alex.gismasterappmvp.base;

import android.app.Application;

import com.example.alex.gismasterappmvp.dagger.AppComponent;
import com.example.alex.gismasterappmvp.dagger.DaggerAppComponent;
import com.example.alex.gismasterappmvp.dagger.modules.ContextModule;

import io.realm.Realm;

public class BaseApplication extends Application {

    private static AppComponent sAppComponent;


    @Override
    public void onCreate(){
        super.onCreate();
        Realm.init(this);
        sAppComponent = DaggerAppComponent.builder()
                .contextModule(new ContextModule(this))
                .build();
    }

    public static AppComponent getsAppComponent(){
        return sAppComponent;
    }
}
