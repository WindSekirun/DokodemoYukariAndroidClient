package com.github.windsekirun.yukarisynthesizer

import android.app.Activity
import android.app.Service
import android.content.Context
import com.github.windsekirun.baseapp.BaseApplication
import com.github.windsekirun.daggerautoinject.DaggerAutoInject
import com.github.windsekirun.daggerautoinject.InjectApplication
import com.github.windsekirun.yukarisynthesizer.core.item.MyObjectBox
import com.github.windsekirun.yukarisynthesizer.di.AppComponent
import com.github.windsekirun.yukarisynthesizer.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.HasServiceInjector
import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.android.AndroidObjectBrowser
import pyxis.uzuki.live.attribute.parser.annotation.AttributeParser
import javax.inject.Inject

/**
 * PyxisBaseApp
 * Class: MainApplication
 * Created by Pyxis on 2018-01-22.
 *
 *
 * Description:
 */
@AttributeParser("com.appg.baseappset")
@InjectApplication(component = AppComponent::class)
class MainApplication : BaseApplication(), HasActivityInjector, HasServiceInjector {
    @Inject lateinit var activityDispatchingAndroidInjector: DispatchingAndroidInjector<Activity>
    @Inject lateinit var serviceDispatchingAndroidInjector: DispatchingAndroidInjector<Service>
    private var mBoxStore: BoxStore? = null

    override val configFilePath: String
        get() = "config.json"

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder()
            .application(this)
            .build()

        DaggerAutoInject.init(this, appComponent!!)

        mBoxStore = MyObjectBox.builder().androidContext(this).build()

        if (BuildConfig.DEBUG) {
//            AndroidObjectBrowser(mBoxStore).start(this)
        }
    }

    fun <T> getBox(cls: Class<T>): Box<T> {
        return mBoxStore!!.boxFor(cls)
    }

    override fun activityInjector(): DispatchingAndroidInjector<Activity>? {
        return activityDispatchingAndroidInjector
    }

    override fun serviceInjector(): AndroidInjector<Service>? {
        return serviceDispatchingAndroidInjector
    }

    companion object {
        /**
         * @return [DaggerAppComponent] to inject
         */
        var appComponent: AppComponent? = null
            private set

        fun getApplication(context: Context): MainApplication {
            return if (context is BaseApplication) {
                context as MainApplication
            } else context.applicationContext as MainApplication

        }
    }
}
