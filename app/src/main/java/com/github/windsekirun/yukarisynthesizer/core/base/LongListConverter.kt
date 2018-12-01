package com.github.windsekirun.yukarisynthesizer.core.base

import com.github.windsekirun.yukarisynthesizer.core.utils.ListParameterizedType
import com.google.gson.Gson
import io.objectbox.converter.PropertyConverter

class LongListConverter : PropertyConverter<List<Long>, String> {
    override fun convertToDatabaseValue(entityProperty: List<Long>?): String? {
        if (entityProperty == null) return null
        val list = entityProperty.map { it.toString() }
        return Gson().toJson(list)
    }

    override fun convertToEntityProperty(databaseValue: String?): List<Long>? {
        if (databaseValue == null) return null
        val list: List<String> = Gson().fromJson(databaseValue, ListParameterizedType(String::class.java))
        return list.map { it.toLong() }
    }
}