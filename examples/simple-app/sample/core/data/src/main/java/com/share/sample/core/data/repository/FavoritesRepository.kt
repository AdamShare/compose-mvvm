package com.share.sample.core.data.repository

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Repository for managing favorite items.
 *
 * Favorites are stored as item IDs in SharedPreferences and exposed
 * as a reactive StateFlow for UI observation.
 */
class FavoritesRepository(
    application: Application
) {
    private val prefs: SharedPreferences = application.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    private val _favorites = MutableStateFlow<Set<String>>(loadFavorites())

    /** Observable set of favorite item IDs. */
    val favorites: StateFlow<Set<String>> = _favorites.asStateFlow()

    /**
     * Returns true if the given item is a favorite.
     */
    fun isFavorite(itemId: String): Boolean {
        return _favorites.value.contains(itemId)
    }

    /**
     * Adds an item to favorites.
     */
    fun addFavorite(itemId: String) {
        val updated = _favorites.value + itemId
        saveFavorites(updated)
        _favorites.value = updated
    }

    /**
     * Removes an item from favorites.
     */
    fun removeFavorite(itemId: String) {
        val updated = _favorites.value - itemId
        saveFavorites(updated)
        _favorites.value = updated
    }

    /**
     * Toggles the favorite status of an item.
     *
     * @return true if the item is now a favorite, false if removed
     */
    fun toggleFavorite(itemId: String): Boolean {
        return if (isFavorite(itemId)) {
            removeFavorite(itemId)
            false
        } else {
            addFavorite(itemId)
            true
        }
    }

    /**
     * Clears all favorites.
     */
    fun clearAll() {
        prefs.edit().remove(KEY_FAVORITES).apply()
        _favorites.value = emptySet()
    }

    private fun loadFavorites(): Set<String> {
        return prefs.getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()
    }

    private fun saveFavorites(favorites: Set<String>) {
        prefs.edit().putStringSet(KEY_FAVORITES, favorites).apply()
    }

    companion object {
        private const val PREFS_NAME = "favorites_prefs"
        private const val KEY_FAVORITES = "favorite_ids"
    }
}
