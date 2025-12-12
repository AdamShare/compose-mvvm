package com.share.sample.core.data.model

import com.share.external.lib.view.ViewKey

/**
 * Categories of content available from the Apple RSS Feed.
 *
 * Each category maps to a specific mediaType, feedType, and resultType in the API.
 * URL format: /api/v2/{country}/{mediaType}/{feedType}/{limit}/{resultType}.json
 */
enum class Category(
    val displayName: String,
    val mediaType: MediaType,
    val feedType: String,
    val resultType: String = mediaType.apiValue // Defaults to mediaType, but can be different (e.g., albums, songs)
) : ViewKey {
    TOP_FREE_APPS("Top Free Apps", MediaType.APPS, "top-free"),
    TOP_PAID_APPS("Top Paid Apps", MediaType.APPS, "top-paid"),
    TOP_ALBUMS("Top Albums", MediaType.MUSIC, "most-played", "albums"),
    TOP_SONGS("Top Songs", MediaType.MUSIC, "most-played", "songs"),
    TOP_PODCASTS("Top Podcasts", MediaType.PODCASTS, "top"),
    TOP_FREE_BOOKS("Top Free Books", MediaType.BOOKS, "top-free"),
    TOP_PAID_BOOKS("Top Paid Books", MediaType.BOOKS, "top-paid");

    companion object {
        val default: Category = TOP_FREE_APPS
    }
}
