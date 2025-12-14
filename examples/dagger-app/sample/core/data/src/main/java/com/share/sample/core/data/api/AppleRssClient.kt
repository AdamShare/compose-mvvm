package com.share.sample.core.data.api

import co.touchlab.kermit.Logger
import com.share.sample.core.data.model.Category
import com.share.sample.core.data.model.MediaType
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * HTTP client for the Apple RSS Feed Marketing Tools API.
 *
 * API format: https://rss.marketingtools.apple.com/api/v2/{country}/{mediaType}/{feedType}/{limit}/{resultType}.json
 */
class AppleRssClient {
    private val log = Logger.withTag(TAG)

    private val client = HttpClient(Android) {
        engine {
            connectTimeout = TIMEOUT_MS.toInt()
            socketTimeout = TIMEOUT_MS.toInt()
        }
        install(HttpTimeout) {
            requestTimeoutMillis = TIMEOUT_MS
            connectTimeoutMillis = TIMEOUT_MS
            socketTimeoutMillis = TIMEOUT_MS
        }
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        install(Logging) {
            level = LogLevel.INFO
            logger = object : io.ktor.client.plugins.logging.Logger {
                override fun log(message: String) {
                    log.d { message }
                }
            }
        }
    }

    /**
     * Fetches the RSS feed for the given category.
     *
     * @param category The content category to fetch
     * @param country ISO country code (default: "us")
     * @param limit Number of results to fetch (default: 25)
     * @return The RSS feed response
     */
    suspend fun getFeed(
        category: Category,
        country: String = DEFAULT_COUNTRY,
        limit: Int = DEFAULT_LIMIT
    ): RssResponse {
        val url = buildUrl(country, category.mediaType.apiValue, category.feedType, category.resultType, limit)
        return client.get(url).body()
    }

    /**
     * Fetches items by a specific artist/creator.
     *
     * Uses the iTunes Search API to find items by artist ID.
     *
     * @param artistId The artist/developer ID
     * @param mediaType The media type to search (apps, music, podcast)
     * @return List of items by this artist
     */
    suspend fun getItemsByArtist(
        artistId: String,
        mediaType: MediaType
    ): ArtistLookupResponse {
        val entity = when (mediaType) {
            MediaType.APPS -> "software"
            MediaType.MUSIC -> "album"
            MediaType.PODCASTS -> "podcast"
            MediaType.BOOKS -> "ebook"
        }
        val url = "$ITUNES_LOOKUP_BASE_URL?id=$artistId&entity=$entity"
        return client.get(url).body()
    }

    private fun buildUrl(
        country: String,
        mediaType: String,
        feedType: String,
        resultType: String,
        limit: Int
    ): String {
        return "$BASE_URL/$country/$mediaType/$feedType/$limit/$resultType.json"
    }

    companion object {
        private const val TAG = "AppleRssClient"
        private const val BASE_URL = "https://rss.marketingtools.apple.com/api/v2"
        private const val ITUNES_LOOKUP_BASE_URL = "https://itunes.apple.com/lookup"
        private const val DEFAULT_COUNTRY = "us"
        private const val DEFAULT_LIMIT = 25
        private const val TIMEOUT_MS = 10_000L
    }
}
