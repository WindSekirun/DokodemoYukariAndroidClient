package com.github.windsekirun.yukarisynthesizer.core.annotation

import androidx.annotation.IntDef
import com.github.windsekirun.yukarisynthesizer.main.details.event.MenuClickBarEvent

@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@IntDef(
    OrderType.OrderFlags.ASCENDING,
    OrderType.OrderFlags.DESCENDING,
    OrderType.OrderFlags.CASE_SENSITIVE,
    OrderType.OrderFlags.NULLS_LAST,
    OrderType.OrderFlags.NULLS_ZERO,
    OrderType.OrderFlags.UNSIGNED
)
annotation class OrderType() {

    object OrderFlags {

        /**
         * Order by Ascending(Default)
         */
        const val ASCENDING = 0

        /**
         * Reverts the order from ascending (default) to descending.
         */
        const val DESCENDING = 1

        /**
         * Makes upper case letters (e.g. "Z") be sorted before lower case letters (e.g. "a").
         * If not specified, the default is case insensitive for ASCII characters.
         */
        const val CASE_SENSITIVE = 2

        /**
         * For scalars only: changes the comparison to unsigned (default is signed).
         */
        const val UNSIGNED = 4

        /**
         * null values will be put last.
         * If not specified, by default null values will be put first.
         */
        const val NULLS_LAST = 8

        /**
         * null values should be treated equal to zero (scalars only).
         */
        const val NULLS_ZERO = 16
    }


}

