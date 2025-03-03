package com.share.external.lib.mvvm.application

import android.app.Application

interface ApplicationProvider {
    fun getApplication(): Application
}