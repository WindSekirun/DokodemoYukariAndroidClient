package com.github.windsekirun.yukarisynthesizer.di

import com.github.windsekirun.yukarisynthesizer.MainApplication
import com.github.windsekirun.yukarisynthesizer.core.YukariOperator
import com.github.windsekirun.yukarisynthesizer.core.repository.PreferenceRepository
import com.github.windsekirun.yukarisynthesizer.core.repository.PreferenceRepositoryImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Dagger module for provide project-specific class
 */
@Module
class AppProvidesModule {

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