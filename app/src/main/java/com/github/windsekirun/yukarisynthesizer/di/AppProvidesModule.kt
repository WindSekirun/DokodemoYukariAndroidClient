package com.github.windsekirun.yukarisynthesizer.di

import com.github.windsekirun.yukarisynthesizer.MainApplication
import com.github.windsekirun.yukarisynthesizer.core.YukariOperator
import com.github.windsekirun.yukarisynthesizer.core.repository.PreferenceRepository
import com.github.windsekirun.yukarisynthesizer.core.repository.PreferenceRepositoryImpl
import com.github.windsekirun.yukarisynthesizer.net.JSONService
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
    fun providePreferenceRepository(impl: PreferenceRepositoryImpl): PreferenceRepository {
        return impl
    }

    @Provides
    @Singleton
    fun providePreferenceRepositoryImpl(context: MainApplication): PreferenceRepositoryImpl {
        return PreferenceRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun provideYukariOperator(context: MainApplication): YukariOperator {
        return YukariOperator(context)
    }
}