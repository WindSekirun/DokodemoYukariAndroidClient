package com.github.windsekirun.yukarisynthesizer.setting

import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.github.windsekirun.baseapp.base.BaseViewModel
import com.github.windsekirun.baseapp.module.reference.ActivityReference
import com.github.windsekirun.daggerautoinject.InjectViewModel
import com.github.windsekirun.yukarisynthesizer.MainApplication
import com.github.windsekirun.yukarisynthesizer.R;
import com.github.windsekirun.yukarisynthesizer.core.repository.PreferenceRepository
import com.github.windsekirun.yukarisynthesizer.ext.CustomTabsExtensions

import javax.inject.Inject

@InjectViewModel
class SettingViewModel @Inject
constructor(application: MainApplication) : BaseViewModel(application) {

    @Inject
    lateinit var preferenceRepository: PreferenceRepository

    fun clickGithub(view: View) {
        CustomTabsExtensions.openWithCustomTabs(getApplication(), CustomTabsExtensions.GITHUB_URL)
    }

    fun clickHistory(view: View) {
        CustomTabsExtensions.openWithCustomTabs(getApplication(), CustomTabsExtensions.HISTORY_URL)
    }

    fun clickLicense(view: View) {
        CustomTabsExtensions.openWithCustomTabs(getApplication(), CustomTabsExtensions.LICENSE_URL)
    }

    fun clickGuide(view: View) {
        CustomTabsExtensions.openWithCustomTabs(getApplication(), CustomTabsExtensions.GUIDE_URL)
    }


    fun clickAPI(view: View) {
        MaterialDialog(checkNotNull(ActivityReference.getActivtyReference())).show {
            input(
                hintRes = R.string.setting_api_key_hint,
                prefill = preferenceRepository.apiKey
            ) { _, text ->
                preferenceRepository.apiKey = text.toString()
                showToast("Saved.")
            }

            positiveButton(R.string.submit)
        }
    }


}