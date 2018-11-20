package com.github.windsekirun.yukarisynthesizer.di

import com.github.windsekirun.yukarisynthesizer.MainApplication
import com.github.windsekirun.yukarisynthesizer.net.JSONService
import com.github.windsekirun.yukarisynthesizer.repository.MainRepository
import com.github.windsekirun.yukarisynthesizer.repository.PreferenceRepository
import com.github.windsekirun.yukarisynthesizer.repository.PreferenceRepositoryImpl
import dagger.Module
import dagger.Provides
import pyxis.uzuki.live.richutilskt.utils.RPreference
import retrofit2.Retrofit

import javax.inject.Singleton

@Module
class AppProvidesModule {

    @Singleton
    @Provides
    fun provideJSONService(retrofit: Retrofit): JSONService {
        return retrofit.create(JSONService::class.java)
    }

    @Provides
    @Singleton
    fun provideRPerference(application: MainApplication): RPreference {
        return RPreference.getInstance(application)
    }

    @Provides
    @Singleton
    fun providePreferenceRepository(impl: PreferenceRepositoryImpl): PreferenceRepository {
        return impl
    }

    @Provides
    @Singleton
    fun providePreferenceRepositoryImpl(application: MainApplication): PreferenceRepositoryImpl {
        return PreferenceRepositoryImpl(application)
    }

    @Provides
    @Singleton
    fun provideMainRepository(service: JSONService): MainRepository {
        return MainRepository(service)
    }
}