package com.github.windsekirun.yukarisynthesizer.ext

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.github.windsekirun.yukarisynthesizer.R

object CustomTabsExtensions {
    fun openWithCustomTabs(context: Context, url: String) {
        val builder = CustomTabsIntent.Builder().apply {
            setToolbarColor(ContextCompat.getColor(context, R.color.primaryColor))
        }
        val intent = builder.build()
        intent.launchUrl(context, Uri.parse(url))
    }

    const val GITHUB_URL = "https://github.com/WindSekirun/DokodemoYukariAndroidClient"
    const val HISTORY_URL =
        "https://github.com/WindSekirun/DokodemoYukariAndroidClient/blob/master/Release.md"
    const val LICENSE_URL =
        "https://github.com/WindSekirun/DokodemoYukariAndroidClient/blob/master/LICENSE.MD"
    const val GUIDE_URL =
        "https://github.com/WindSekirun/DokodemoYukariAndroidClient/blob/master/Guide.md"
}