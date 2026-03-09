package com.share.sample.core.data.repository

/**
 * In-memory implementation of favorites storage for Desktop.
 *
 * Favorites are lost when the application exits.
 * In a real app, this could use a file-based or database-backed storage.
 */
class InMemoryFavoritesStorage : FavoritesStorage {
    @Volatile
    private var favorites: Set<String> = emptySet()

    override fun loadFavorites(): Set<String> = favorites

    override fun saveFavorites(favorites: Set<String>) {
        this.favorites = favorites
    }
}
