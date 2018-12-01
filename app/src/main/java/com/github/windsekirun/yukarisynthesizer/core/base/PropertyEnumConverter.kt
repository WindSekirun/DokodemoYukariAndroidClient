package com.github.windsekirun.yukarisynthesizer.core.base

import io.objectbox.converter.PropertyConverter

open class PropertyEnumConverter<T, R>(
    private val values: Array<T>, private val default: T,
    private val predicate: T.(R) -> Boolean,
    private val supplier: (T) -> R?
) : PropertyConverter<T, R> {

    override fun convertToEntityProperty(databaseValue: R?): T? {
        if (databaseValue == null) return null
        for (state in values) {
            if (state.predicate(databaseValue)) {
                return state
            }
        }
        return default
    }

    override fun convertToDatabaseValue(entityProperty: T?): R? {
        if (entityProperty == null) return null
        return supplier(entityProperty)
    }
}