package com.share.sample.core.data.api

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.java.Java

/**
 * Desktop (JVM) implementation of the HTTP client engine.
 */
internal actual fun getPlatformEngine(): HttpClientEngine {
    return Java.create()
}
