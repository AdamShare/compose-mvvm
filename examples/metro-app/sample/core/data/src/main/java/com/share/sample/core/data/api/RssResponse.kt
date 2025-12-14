package com.share.sample.core.data.api

import com.share.sample.core.data.model.FeedItem
import kotlinx.serialization.Serializable

/**
 * Root response from the Apple RSS Feed API.
 */
@Serializable
data class RssResponse(
    val feed: Feed
)

/**
 * Feed container with metadata and results.
 */
@Serializable
data class Feed(
    val title: String,
    val id: String,
    val copyright: String,
    val country: String,
    val icon: String? = null,
    val updated: String,
    val results: List<FeedItem>
)
