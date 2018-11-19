package com.github.windsekirun.yukarisynthesizer.core.base

import com.google.gson.Gson
import io.objectbox.converter.PropertyConverter

open class PropertyJsonConverter<T>(private val cls: Class<T>) : PropertyConverter<T, String> {
    override fun convertToEntityProperty(databaseValue: String?): T? {
        if (databaseValue == null) return null
        return Gson().fromJson(databaseValue, cls) as T
    }

    override fun convertToDatabaseValue(entityProperty: T?): String? {
        if (entityProperty == null) return null
        return Gson().toJson(entityProperty)
    }
}