package com.github.windsekirun.yukarisynthesizer.repository;

import com.github.windsekirun.yukarisynthesizer.MainApplication;
import pyxis.uzuki.live.richutilskt.utils.RPreference;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * PyxisBaseApp
 * Class: PreferenceRepositoryImpl
 * Created by Pyxis on 2018-02-02.
 * <p>
 * Description:
 */

@Singleton
public class PreferenceRepositoryImpl implements PreferenceRepository {
    private RPreference mPreference;

    private static final String REGISTRATION_KEY = "1af49084-8820-449e-9039-d86863184592";

    @Inject
    public PreferenceRepositoryImpl(MainApplication application) {
        mPreference = RPreference.getInstance(application);
    }

    @Override
    public String getRegistrationKey() {
        return mPreference.getString(REGISTRATION_KEY, "");
    }

    @Override
    public void setRegistrationKey(String registrationKey) {
        mPreference.put(REGISTRATION_KEY, registrationKey);
    }
}
