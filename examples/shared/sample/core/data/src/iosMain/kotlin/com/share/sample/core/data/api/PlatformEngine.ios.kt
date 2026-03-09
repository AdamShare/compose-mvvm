package com.share.sample.core.data.api

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin

internal actual fun getPlatformEngine(): HttpClientEngine {
    return Darwin.create()
}
