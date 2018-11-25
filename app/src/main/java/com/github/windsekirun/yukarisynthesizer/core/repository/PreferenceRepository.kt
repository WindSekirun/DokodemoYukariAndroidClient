package com.github.windsekirun.yukarisynthesizer.core.repository

import com.github.windsekirun.baseapp.repository.impl.BasePreferenceRepository

/**
 * PyxisBaseApp
 * Class: PreferenceRepository
 * Created by Pyxis on 2018-02-02.
 *
 *
 * Description:
 */

interface PreferenceRepository : BasePreferenceRepository {

    var apiKey: String
}
