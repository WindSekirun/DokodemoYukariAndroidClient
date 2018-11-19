package com.github.windsekirun.yukarisynthesizer.di;

import android.app.Application;
import com.github.windsekirun.yukarisynthesizer.MainApplication;
import dagger.Binds;
import dagger.Module;

@Module
public abstract class AppBindsModule {

    @Binds
    abstract Application bindApplication(MainApplication application);
}