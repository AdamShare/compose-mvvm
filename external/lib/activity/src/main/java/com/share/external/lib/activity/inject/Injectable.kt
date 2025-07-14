package com.share.external.lib.activity.inject

interface Injectable<T> {
    fun inject(instance: T)
}

fun <T, I : Injectable<T>> ((T) -> I).inject(instance: T) = this(instance).inject(instance)
