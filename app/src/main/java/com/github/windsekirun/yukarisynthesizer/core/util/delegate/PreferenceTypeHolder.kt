package com.github.windsekirun.yukarisynthesizer.core.util.delegate

import android.content.Context
import pyxis.uzuki.live.richutilskt.utils.RPreference
import java.lang.IllegalArgumentException
import kotlin.reflect.KProperty

class PreferenceTypeHolder<T : Any>(val context: Context, val key: String, val defaultValue: T?) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        val preference = RPreference.getInstance(context)
        val result: Any = when (defaultValue) {
            is String -> preference.getString(key, defaultValue)
            is CharSequence -> preference.getCharSequence(key, defaultValue)
            is Boolean -> preference.getBoolean(key, defaultValue)
            is Int -> preference.getInt(key, defaultValue)
            is Long -> preference.getLong(key, defaultValue)
            is Float -> preference.getFloat(key, defaultValue)
            is Double -> preference.getDouble(key, defaultValue)
            is Char -> preference.getChar(key, defaultValue)
            else -> throw IllegalArgumentException("Not supported type")
        }

        return result as T
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        val preference = RPreference.getInstance(context)
        when (value) {
            is String -> preference.put(key, value)
            is CharSequence -> preference.put(key, value)
            is Boolean -> preference.put(key, value)
            is Int -> preference.put(key, value)
            is Long -> preference.put(key, value)
            is Float -> preference.put(key, value)
            is Double -> preference.put(key, value)
            is Char -> preference.put(key, value)
        }
    }
}

