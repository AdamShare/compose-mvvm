package com.share.sample.core.data.api

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.js.Js

internal actual fun getPlatformEngine(): HttpClientEngine {
    return Js.create()
}
