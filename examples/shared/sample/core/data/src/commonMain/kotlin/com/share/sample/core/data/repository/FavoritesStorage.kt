package com.share.sample.core.data.repository

/**
 * Platform-agnostic interface for favorites storage.
 */
interface FavoritesStorage {
    fun loadFavorites(): Set<String>
    fun saveFavorites(favorites: Set<String>)
}
