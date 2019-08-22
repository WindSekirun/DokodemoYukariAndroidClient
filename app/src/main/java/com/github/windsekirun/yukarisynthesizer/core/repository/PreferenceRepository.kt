package com.github.windsekirun.yukarisynthesizer.core.repository

import com.github.windsekirun.baseapp.repository.impl.BasePreferenceRepository

interface PreferenceRepository : BasePreferenceRepository {

    var apiKey: String
    var newUser: Boolean
}
