package com.share.sample.core.data.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * The type of content from the Apple RSS Feed.
 */
@Serializable(with = ContentKind.Serializer::class)
enum class ContentKind(val apiValue: String, val mediaType: MediaType) {
    APPS("apps", MediaType.APPS),
    ALBUMS("albums", MediaType.MUSIC),
    SONGS("songs", MediaType.MUSIC),
    PODCASTS("podcasts", MediaType.PODCASTS),
    BOOKS("books", MediaType.BOOKS);

    internal object Serializer : KSerializer<ContentKind?> {
        override val descriptor = PrimitiveSerialDescriptor("ContentKind", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: ContentKind?) {
            encoder.encodeString(value?.apiValue ?: "")
        }

        override fun deserialize(decoder: Decoder): ContentKind? {
            val value = decoder.decodeString()
            return entries.find { it.apiValue == value }
        }
    }
}

/**
 * Media type categories for the Apple RSS Feed API.
 */
enum class MediaType(val apiValue: String) {
    APPS("apps"),
    MUSIC("music"),
    PODCASTS("podcasts"),
    BOOKS("books");
}

/**
 * A single item from the Apple RSS Feed.
 * Represents an app, album, song, or podcast.
 */
@Serializable
data class FeedItem(
    val id: String,
    val name: String,
    val artistName: String,
    val artistId: String? = null,
    val artistUrl: String? = null,
    val artworkUrl100: String,
    val url: String,
    val releaseDate: String? = null,
    val kind: ContentKind? = null,
    val genres: List<Genre> = emptyList()
) {
    /** The media type derived from the item's kind. */
    val mediaType: MediaType
        get() = kind?.mediaType ?: MediaType.APPS

    /** Higher resolution artwork URL (500px). */
    val artworkUrl500: String
        get() = artworkUrl100.replace("100x100", "500x500")

    /** Higher resolution artwork URL (200px). */
    val artworkUrl200: String
        get() = artworkUrl100.replace("100x100", "200x200")
}

/**
 * Genre information for a feed item.
 */
@Serializable
data class Genre(
    val genreId: String,
    val name: String,
    val url: String
)

/**
 * Creator (artist/developer) information.
 */
data class Creator(
    val id: String,
    val name: String
)
