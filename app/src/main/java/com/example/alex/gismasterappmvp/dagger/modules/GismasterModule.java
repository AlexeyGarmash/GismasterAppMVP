package com.example.alex.gismasterappmvp.dagger.modules;

import com.example.alex.gismasterappmvp.retrofit.GismasterApi;
import com.example.alex.gismasterappmvp.retrofit.GismasterService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = {ApiModule.class})
public class GismasterModule {

    @Provides
    @Singleton
    public GismasterService provideGismasterService(GismasterApi api){
        return new GismasterService(api);
    }
}
