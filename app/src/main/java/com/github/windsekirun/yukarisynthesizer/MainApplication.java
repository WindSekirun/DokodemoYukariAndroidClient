package com.github.windsekirun.yukarisynthesizer;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import com.github.windsekirun.yukarisynthesizer.di.DaggerAppComponent;
import com.github.windsekirun.baseapp.BaseApplication;
import com.github.windsekirun.daggerautoinject.DaggerAutoInject;

import com.github.windsekirun.daggerautoinject.InjectApplication;
import com.github.windsekirun.yukarisynthesizer.di.AppComponent;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.HasServiceInjector;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import pyxis.uzuki.live.attribute.parser.annotation.AttributeParser;

import javax.inject.Inject;

/**
 * PyxisBaseApp
 * Class: MainApplication
 * Created by Pyxis on 2018-01-22.
 * <p>
 * Description:
 */
@AttributeParser("com.appg.baseappset")
@InjectApplication(component = AppComponent.class)
public class MainApplication extends BaseApplication implements HasActivityInjector, HasServiceInjector {
    @Inject
    DispatchingAndroidInjector<Activity> mActivityDispatchingAndroidInjector;
    @Inject
    DispatchingAndroidInjector<Service> mServiceDispatchingAndroidInjector;
    private static AppComponent mAppComponent;
    private BoxStore mBoxStore;

    @Override
    public String getConfigFilePath() {
        return "config.json";
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mAppComponent = DaggerAppComponent.builder()
                .application(this)
                .build();

        DaggerAutoInject.init(this, mAppComponent);

//        mBoxStore = MyObjectBox.builder().androidContext(this).build();
    }

    @SuppressWarnings("unchecked")
    public static MainApplication getApplication(Context context) {
        if (context instanceof BaseApplication) {
            return (MainApplication) context;
        }

        return (MainApplication) context.getApplicationContext();
    }

    public <T> Box<T> getBox(Class<T> cls) {         return mBoxStore.boxFor(cls);     }

    @Override
    public DispatchingAndroidInjector<Activity> activityInjector() {
        return mActivityDispatchingAndroidInjector;
    }

    @Override
    public AndroidInjector<Service> serviceInjector() {
        return mServiceDispatchingAndroidInjector;
    }


    /**
     * @return {@link DaggerAppComponent} to inject
     */
    public static AppComponent getAppComponent() {
        return mAppComponent;
    }
}
