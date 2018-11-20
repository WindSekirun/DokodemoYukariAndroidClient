package com.github.windsekirun.yukarisynthesizer.repository

import com.github.windsekirun.yukarisynthesizer.MainApplication
import pyxis.uzuki.live.richutilskt.utils.RPreference

import javax.inject.Inject
import javax.inject.Singleton

/**
 * PyxisBaseApp
 * Class: PreferenceRepositoryImpl
 * Created by Pyxis on 2018-02-02.
 *
 *
 * Description:
 */

@Singleton
class PreferenceRepositoryImpl @Inject
constructor(application: MainApplication) : PreferenceRepository {
    private val preference: RPreference = RPreference.getInstance(application)

    override var registrationKey: String
        get() = preference.getString(REGISTRATION_KEY, "")
        set(registrationKey) {
            preference.put(REGISTRATION_KEY, registrationKey)
        }

    companion object {
        private val REGISTRATION_KEY = "1af49084-8820-449e-9039-d86863184592"
    }
}
