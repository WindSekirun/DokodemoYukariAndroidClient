package com.github.windsekirun.yukarisynthesizer.core.repository

import android.content.Context
import com.github.windsekirun.yukarisynthesizer.utils.PreferenceTypeHolder
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
constructor(context: Context) : PreferenceRepository {
    override var registrationKey: String by PreferenceTypeHolder(context, REGISTRATION_KEY, "")
    override var apiKey: String by PreferenceTypeHolder(
        context,
        API_KEY,
        "67375458343230674173395a64654730682f61774830736e39597859634331476d4276496b756a6e467544"
    )
    override var newUser: Boolean by PreferenceTypeHolder(context, NEW_USER, true)

    companion object {
        private val REGISTRATION_KEY = "1af49084-8820-449e-9039-d86863184592"
        private val API_KEY = "c72c2903-89b2-47a4-835c-bbb9b1384325"
        private val NEW_USER = "c72c2903-89b2-47a4-835c-ds9b1384325"
    }
}
