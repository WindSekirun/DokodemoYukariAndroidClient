package com.github.windsekirun.yukarisynthesizer.core.utils

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type


class ListParameterizedType<X>(wrapped: Class<X>) : ParameterizedType {

    private val wrapped: Class<*>

    init {
        this.wrapped = wrapped
    }

    override fun getActualTypeArguments(): Array<Type> {
        return arrayOf(wrapped)
    }

    override fun getRawType(): Type {
        return List::class.java
    }

    override fun getOwnerType(): Type? {
        return null
    }

}