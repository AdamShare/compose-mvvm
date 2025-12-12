package com.share.sample.core.data.repository

import co.touchlab.kermit.Logger
import com.share.sample.core.data.api.AppleRssClient
import com.share.sample.core.data.api.ArtistResult
import com.share.sample.core.data.model.Category
import com.share.sample.core.data.model.Creator
import com.share.sample.core.data.model.FeedItem
import com.share.sample.core.data.model.MediaType

/**
 * Repository for fetching RSS feed data from Apple.
 *
 * Provides methods to fetch feeds by category and look up artist information.
 */
class FeedRepository(
    private val client: AppleRssClient
) {
    private val log = Logger.withTag(TAG)

    /**
     * Fetches the feed for the given category.
     *
     * @param category The content category to fetch
     * @return Result containing the list of feed items or an error
     */
    suspend fun getFeed(category: Category): Result<List<FeedItem>> {
        return try {
            val response = client.getFeed(category)
            Result.success(response.feed.results)
        } catch (e: Exception) {
            log.e(e) { "Failed to fetch feed for category: $category" }
            Result.failure(e)
        }
    }

    /**
     * Fetches other items by the same artist/creator.
     *
     * @param creator The creator to look up
     * @param mediaType The media type for context (apps, music, podcasts)
     * @return Result containing other items by this creator
     */
    suspend fun getItemsByCreator(
        creator: Creator,
        mediaType: MediaType
    ): Result<List<ArtistResult>> {
        return try {
            val response = client.getItemsByArtist(creator.id, mediaType)
            // Filter out the artist entry itself, keeping only works
            val works = response.results.filter { !it.isArtist }
            Result.success(works)
        } catch (e: Exception) {
            log.e(e) { "Failed to fetch items for creator: ${creator.name} (${creator.id})" }
            Result.failure(e)
        }
    }

    /**
     * Fetches a single feed item by ID.
     *
     * Note: The Apple RSS API doesn't support fetching single items directly.
     * This method searches through the feed to find the matching item.
     *
     * @param itemId The ID of the item to fetch
     * @param category The category to search in
     * @return Result containing the feed item or an error
     */
    suspend fun getItemById(
        itemId: String,
        category: Category
    ): Result<FeedItem> {
        return try {
            val response = client.getFeed(category, limit = 100)
            val item = response.feed.results.find { it.id == itemId }
            if (item != null) {
                Result.success(item)
            } else {
                log.w { "Item not found: $itemId in category $category" }
                Result.failure(NoSuchElementException("Item not found: $itemId"))
            }
        } catch (e: Exception) {
            log.e(e) { "Failed to fetch item by ID: $itemId" }
            Result.failure(e)
        }
    }

    companion object {
        private const val TAG = "FeedRepository"
    }
}
