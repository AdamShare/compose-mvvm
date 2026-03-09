package com.share.sample.core.data.api

import kotlinx.serialization.Serializable

/**
 * Response from the iTunes Lookup API for artist/creator details.
 */
@Serializable
data class ArtistLookupResponse(
    val resultCount: Int,
    val results: List<ArtistResult>
)

/**
 * Individual result from iTunes lookup.
 * This can represent an artist or their works (apps, albums, etc.)
 */
@Serializable
data class ArtistResult(
    val wrapperType: String? = null,
    val artistType: String? = null,
    val artistName: String? = null,
    val artistId: Long? = null,
    val artistLinkUrl: String? = null,
    // For software/apps
    val trackId: Long? = null,
    val trackName: String? = null,
    val bundleId: String? = null,
    val artworkUrl100: String? = null,
    val artworkUrl512: String? = null,
    // For albums/music
    val collectionId: Long? = null,
    val collectionName: String? = null,
    val collectionViewUrl: String? = null,
    // Common fields
    val primaryGenreName: String? = null,
    val releaseDate: String? = null
) {
    /** Gets the display name for this result. */
    val displayName: String
        get() = trackName ?: collectionName ?: artistName ?: "Unknown"

    /** Gets the artwork URL, preferring higher resolution. */
    val artworkUrl: String?
        get() = artworkUrl512 ?: artworkUrl100

    /** Gets the unique ID for this result. */
    val id: String
        get() = (trackId ?: collectionId ?: artistId)?.toString() ?: ""

    /** Returns true if this is an artist entry (vs. a work by the artist). */
    val isArtist: Boolean
        get() = wrapperType == "artist"

    /** Converts this result to a FeedItem for navigation. */
    fun toFeedItem(): com.share.sample.core.data.model.FeedItem? {
        if (isArtist) return null
        val itemId = id.takeIf { it.isNotEmpty() } ?: return null
        val itemName = displayName.takeIf { it != "Unknown" } ?: return null

        return com.share.sample.core.data.model.FeedItem(
            id = itemId,
            name = itemName,
            artistName = artistName ?: "",
            artistId = artistId?.toString(),
            artworkUrl100 = artworkUrl100 ?: artworkUrl512?.replace("512x512", "100x100") ?: "",
            url = artistLinkUrl ?: collectionViewUrl ?: "",
            releaseDate = releaseDate,
            genres = primaryGenreName?.let {
                listOf(com.share.sample.core.data.model.Genre(genreId = "", name = it, url = ""))
            } ?: emptyList()
        )
    }
}
