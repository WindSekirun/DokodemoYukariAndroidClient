package com.github.windsekirun.yukarisynthesizer.service;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import androidx.annotation.Nullable;
import com.github.windsekirun.baseapp.module.location.RxLocationTracker;
import com.github.windsekirun.daggerautoinject.InjectService;
import com.github.windsekirun.yukarisynthesizer.repository.PreferenceRepository;
import dagger.android.AndroidInjection;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import javax.inject.Inject;

@InjectService
public class LocationTrackingService extends Service implements Consumer<Location> {
    @Inject RxLocationTracker mLocationTracker;
    @Inject PreferenceRepository mPreferenceRepository;

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    public static final String TAG = LocationTrackingService.class.getSimpleName();

    @Override
    public void onCreate() {
        AndroidInjection.inject(this);
        super.onCreate();
        mLocationTracker.setUpdateInterval(8 * 60 * 1000, 5 * 1000);
        Disposable disposable = mLocationTracker.getUpdateLocationCallback().subscribe(this, error -> {
        });
        mLocationTracker.startTracking();

        mCompositeDisposable.add(disposable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationTracker.stopTracking();
        mCompositeDisposable.clear();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void accept(Location location) throws Exception {

    }

    public static void startService(Activity activity) {
        Intent intent = new Intent(activity, LocationTrackingService.class);
        activity.startService(intent);
    }

    public static void stopService(Activity activity) {
        Intent intent = new Intent(activity, LocationTrackingService.class);
        activity.stopService(intent);
    }
}