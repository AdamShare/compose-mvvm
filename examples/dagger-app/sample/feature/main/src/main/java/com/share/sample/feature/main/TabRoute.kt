package com.share.sample.feature.main

import com.getbackcompose.core.ViewKey

/**
 * Routes for the main tab navigation.
 */
enum class TabRoute : ViewKey {
    Home,
    Favorites,
    Profile;

    companion object {
        val default: TabRoute get() = Home
    }
}
