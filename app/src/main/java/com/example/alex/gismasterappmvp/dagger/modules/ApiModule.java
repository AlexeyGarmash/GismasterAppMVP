package com.example.alex.gismasterappmvp.dagger.modules;


import com.example.alex.gismasterappmvp.retrofit.GismasterApi;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

@Module (includes = {RetrofitModule.class})
public class ApiModule {

    @Provides
    @Singleton
    public GismasterApi provideGismasterApi(Retrofit retrofit){
        return retrofit.create(GismasterApi.class);
    }
}
