package com.share.external.lib.mvvm.navigation.stack

import com.share.external.lib.mvvm.navigation.content.NavigationKey

interface NavigationStack<V>: NavigationBackStack {
    /**
     * Pushes a new [NavigationKey] and returns the value produced by [content]. If the key already exists it is
     * **replaced** and the old entry is cancelled after predictive‑back animations complete.
     */
    fun push(key: NavigationKey, content: (NavigationStackEntry<V>) -> V)

    fun <T> push(content: T) where T : NavigationKey, T : (NavigationStackEntry<V>) -> V {
        push(key = content, content = content)
    }
}
