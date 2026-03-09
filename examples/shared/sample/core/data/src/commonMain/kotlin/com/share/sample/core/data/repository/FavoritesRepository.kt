package com.share.sample.core.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Repository for managing favorite items.
 *
 * Favorites are stored via platform-specific storage and exposed
 * as a reactive StateFlow for UI observation.
 */
class FavoritesRepository(
    private val storage: FavoritesStorage
) {
    private val _favorites = MutableStateFlow(storage.loadFavorites())

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
        _favorites.update { it + itemId }
        storage.saveFavorites(_favorites.value)
    }

    /**
     * Removes an item from favorites.
     */
    fun removeFavorite(itemId: String) {
        _favorites.update { it - itemId }
        storage.saveFavorites(_favorites.value)
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
}
