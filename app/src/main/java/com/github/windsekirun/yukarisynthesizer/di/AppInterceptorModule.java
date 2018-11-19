package com.github.windsekirun.yukarisynthesizer.di;

import com.github.windsekirun.yukarisynthesizer.net.RWInterceptor;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import okhttp3.Interceptor;

/**
 * BaseApp-BasicSet
 * Class: AppInterceptorModule
 * Created by winds on 2018-10-29.
 * <p>
 * Description:
 */
@Module
public class AppInterceptorModule {

    @Provides
    @IntoSet
    public Interceptor provideRWProvider() {
        return new RWInterceptor();
    }
}
