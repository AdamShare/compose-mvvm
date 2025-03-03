package com.share.external.lib.mvvm

interface Injectable<T> {
    fun inject(instance: T)
}

fun <T, I: Injectable<T>> ((T) -> I).inject(instance: T) = this(instance).inject(instance)