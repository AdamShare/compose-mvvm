package com.share.external.lib.activity.application

import android.app.Application

/**
 * Contract for components that can provide access to the [Application] instance.
 *
 * Typically implemented by Activities to provide access to the Application for
 * dependency injection purposes.
 */
interface ApplicationProvider {
    /**
     * Returns the [Application] instance.
     */
    fun getApplication(): Application
}
