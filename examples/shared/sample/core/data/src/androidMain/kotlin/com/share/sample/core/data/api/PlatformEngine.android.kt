package com.share.sample.core.data.api

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.android.Android

/**
 * Android implementation of the HTTP client engine.
 */
internal actual fun getPlatformEngine(): HttpClientEngine {
    return Android.create {
        connectTimeout = 10_000
        socketTimeout = 10_000
    }
}
