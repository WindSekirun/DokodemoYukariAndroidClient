package com.github.windsekirun.yukarisynthesizer.service

import android.app.Activity
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.IBinder
import com.github.windsekirun.baseapp.module.location.RxLocationTracker
import com.github.windsekirun.daggerautoinject.InjectService
import com.github.windsekirun.yukarisynthesizer.repository.PreferenceRepository
import com.github.windsekirun.yukarisynthesizer.utils.CommonUtils
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer

import javax.inject.Inject

@InjectService
class LocationTrackingService : Service(), Consumer<Location> {
    @Inject lateinit var locationTracker: RxLocationTracker
    @Inject lateinit var preferenceRepository: PreferenceRepository

    private val mCompositeDisposable = CompositeDisposable()

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
        locationTracker.setUpdateInterval((8 * 60 * 1000).toLong(), (5 * 1000).toFloat())
        val disposable = locationTracker.getUpdateLocationCallback().subscribe(this, CommonUtils.ignoreError)
        locationTracker.startTracking()

        mCompositeDisposable.add(disposable)
    }

    override fun onDestroy() {
        super.onDestroy()
        locationTracker.stopTracking()
        mCompositeDisposable.clear()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    @Throws(Exception::class)
    override fun accept(location: Location) {

    }

    companion object {
        val TAG = LocationTrackingService::class.java.simpleName

        fun startService(activity: Activity) {
            val intent = Intent(activity, LocationTrackingService::class.java)
            activity.startService(intent)
        }

        fun stopService(activity: Activity) {
            val intent = Intent(activity, LocationTrackingService::class.java)
            activity.stopService(intent)
        }
    }
}