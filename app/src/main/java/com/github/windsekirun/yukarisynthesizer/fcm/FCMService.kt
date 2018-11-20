package com.github.windsekirun.yukarisynthesizer.fcm


import android.app.Activity
import android.content.Intent
import com.github.windsekirun.daggerautoinject.InjectService
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import com.github.windsekirun.yukarisynthesizer.repository.PreferenceRepository
import dagger.android.AndroidInjection
import pyxis.uzuki.live.nyancat.NyanCat

import javax.inject.Inject

@InjectService
class FCMService : FirebaseInstanceIdService() {
    @Inject lateinit var preferenceRepository: PreferenceRepository

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
        val token = FirebaseInstanceId.getInstance().token
        if (token != null && !token.isEmpty()) {
            registerToken(token)
        }
    }

    override fun onTokenRefresh() {
        super.onTokenRefresh()
        val token = FirebaseInstanceId.getInstance().token
        if (token != null && !token.isEmpty()) {
            registerToken(token)
        }
    }

    private fun registerToken(token: String) {
        NyanCat.tag("FCM").d("Refreshed Token: %s", token)
        preferenceRepository.registrationKey = token
    }

    companion object {

        fun startService(activity: Activity) {
            val intent = Intent(activity, FCMService::class.java)
            activity.startService(intent)
        }
    }
}
