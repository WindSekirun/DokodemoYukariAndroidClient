package com.github.windsekirun.yukarisynthesizer.setting

import android.net.Uri
import android.view.View
import androidx.browser.customtabs.CustomTabsIntent
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.github.windsekirun.baseapp.base.BaseViewModel
import com.github.windsekirun.baseapp.module.reference.ActivityReference
import com.github.windsekirun.daggerautoinject.InjectViewModel
import com.github.windsekirun.yukarisynthesizer.MainApplication
import com.github.windsekirun.yukarisynthesizer.R;
import com.github.windsekirun.yukarisynthesizer.core.repository.PreferenceRepository

import javax.inject.Inject

/**
 * DokodemoYukariAndroidClient
 * Class: SettingViewModel
 * Created by Pyxis on 2018-12-19.
 *
 *
 * Description:
 */

@InjectViewModel
class SettingViewModel @Inject
constructor(application: MainApplication) : BaseViewModel(application) {

    @Inject
    lateinit var preferenceRepository: PreferenceRepository

    fun clickGithub(view: View) {
        openWithCustomTabs(GITHUB_URL)
    }

    fun clickHistory(view: View) {
        openWithCustomTabs(HISTORY_URL)
    }

    fun clickLicense(view: View) {
        openWithCustomTabs(LICENSE_URL)
    }

    fun clickAPI(view: View) {
        MaterialDialog(checkNotNull(ActivityReference.getActivtyReference())).show {
            input(hintRes = R.string.setting_api_key_hint, prefill = preferenceRepository.apiKey) { _, text ->
                preferenceRepository.apiKey = text.toString()
                showToast("Saved.")
            }

            positiveButton(R.string.submit)
        }
    }

    private fun openWithCustomTabs(url: String) {
        val builder = CustomTabsIntent.Builder().apply {
            setToolbarColor(getColor(R.color.primaryColor))
        }
        val intent = builder.build()
        intent.launchUrl(getApplication(), Uri.parse(url))
    }

    companion object {
        const val GITHUB_URL = "https://github.com/WindSekirun/DokodemoYukariAndroidClient"
        const val HISTORY_URL = "https://github.com/WindSekirun/DokodemoYukariAndroidClient/blob/master/Release.md"
        const val LICENSE_URL = "https://github.com/WindSekirun/DokodemoYukariAndroidClient/blob/master/LICENSE.MD"
    }
}