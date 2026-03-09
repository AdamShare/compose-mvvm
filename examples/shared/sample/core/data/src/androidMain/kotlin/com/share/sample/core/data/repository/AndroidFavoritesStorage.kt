package com.share.sample.core.data.repository

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * Android implementation of favorites storage using SharedPreferences.
 */
class AndroidFavoritesStorage(
    application: Application
) : FavoritesStorage {
    private val prefs: SharedPreferences = application.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    override fun loadFavorites(): Set<String> {
        return prefs.getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()
    }

    override fun saveFavorites(favorites: Set<String>) {
        prefs.edit { putStringSet(KEY_FAVORITES, favorites) }
    }

    companion object {
        private const val PREFS_NAME = "favorites_prefs"
        private const val KEY_FAVORITES = "favorite_ids"
    }
}
