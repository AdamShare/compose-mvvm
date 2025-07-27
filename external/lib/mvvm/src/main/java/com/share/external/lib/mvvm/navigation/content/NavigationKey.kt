package com.share.external.lib.mvvm.navigation.content

/** Represents a unique navigation destination identifier used for analytics or logging. */
interface NavigationKey {
    /** A stable identifier used for tracking screen transitions or visibility. */
    val name: String
}
