package com.github.windsekirun.yukarisynthesizer.fcm;


import android.app.Activity;
import android.content.Intent;
import com.github.windsekirun.daggerautoinject.InjectService;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.github.windsekirun.yukarisynthesizer.repository.PreferenceRepository;
import dagger.android.AndroidInjection;
import pyxis.uzuki.live.nyancat.NyanCat;

import javax.inject.Inject;

@InjectService
public class FCMService extends FirebaseInstanceIdService {
    @Inject
    PreferenceRepository mPreferenceRepository;

    @Override
    public void onCreate() {
        AndroidInjection.inject(this);
        super.onCreate();
        String token = FirebaseInstanceId.getInstance().getToken();
        if (token != null && !token.isEmpty()) {
            registerToken(token);
        }
    }

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String token = FirebaseInstanceId.getInstance().getToken();
        if (token != null && !token.isEmpty()) {
            registerToken(token);
        }
    }

    private void registerToken(String token) {
        NyanCat.tag("FCM").d("Refreshed Token: %s", token);
        mPreferenceRepository.setRegistrationKey(token);
    }

    public static void startService(Activity activity) {
        Intent intent = new Intent(activity, FCMService.class);
        activity.startService(intent);
    }
}
