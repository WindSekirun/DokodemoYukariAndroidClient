package com.github.windsekirun.yukarisynthesizer.core.base

import com.github.windsekirun.yukarisynthesizer.core.utils.ListParameterizedType
import com.google.gson.Gson
import io.objectbox.converter.PropertyConverter

open class PropertyListConverter<T>(private val cls: Class<T>) : PropertyConverter<List<T>, String> {
    override fun convertToEntityProperty(databaseValue: String?): List<T>? {
        if (databaseValue == null) return null
        return Gson().fromJson(databaseValue, ListParameterizedType(cls))
    }

    override fun convertToDatabaseValue(entityProperty: List<T>?): String? {
        if (entityProperty == null) return null
        return Gson().toJson(entityProperty)
    }
}

