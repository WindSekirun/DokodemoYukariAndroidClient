package com.github.windsekirun.yukarisynthesizer.di;

import com.github.windsekirun.yukarisynthesizer.MainApplication;
import com.github.windsekirun.yukarisynthesizer.net.JSONService;
import com.github.windsekirun.yukarisynthesizer.repository.MainRepository;
import com.github.windsekirun.yukarisynthesizer.repository.PreferenceRepository;
import com.github.windsekirun.yukarisynthesizer.repository.PreferenceRepositoryImpl;
import dagger.Module;
import dagger.Provides;
import pyxis.uzuki.live.richutilskt.utils.RPreference;
import retrofit2.Retrofit;

import javax.inject.Singleton;

@Module
public class AppProvidesModule {

    @Singleton
    @Provides
    JSONService provideJSONService(Retrofit retrofit) {
        return retrofit.create(JSONService.class);
    }

    @Provides
    @Singleton
    public RPreference provideRPerference(MainApplication application) {
        return RPreference.getInstance(application);
    }

    @Provides
    @Singleton
    PreferenceRepository providePreferenceRepository(PreferenceRepositoryImpl impl) {
        return impl;
    }

    @Provides
    @Singleton
    PreferenceRepositoryImpl providePreferenceRepositoryImpl(MainApplication application) {
        return new PreferenceRepositoryImpl(application);
    }

    @Provides
    @Singleton
    MainRepository provideMainRepository(JSONService service) {
        return new MainRepository(service);
    }
}