package com.github.windsekirun.yukarisynthesizer.core.annotation

import androidx.annotation.IntDef

@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@IntDef(
    OrderType.OrderFlags.ASCENDING,
    OrderType.OrderFlags.DESCENDING,
    OrderType.OrderFlags.CASE_SENSITIVE,
    OrderType.OrderFlags.NULLS_LAST,
    OrderType.OrderFlags.NULLS_ZERO,
    OrderType.OrderFlags.UNSIGNED
)
annotation class OrderType {

    object OrderFlags {

        const val ASCENDING = 0

        const val DESCENDING = 1

        const val CASE_SENSITIVE = 2
        const val UNSIGNED = 4
        const val NULLS_LAST = 8

        const val NULLS_ZERO = 16
    }


}

