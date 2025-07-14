package com.share.external.lib.activity.application

import android.app.Application

interface ApplicationProvider {
    fun getApplication(): Application
}
