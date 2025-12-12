@file:Suppress("MaximumLineLength")

package com.share.external.lib.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable

/**
 * A wrapper around a composable function that represents a discrete UI element.
 *
 * This interface provides a stable reference to composable content, enabling views to be
 * passed around, stored, and composed at different points in the composition hierarchy.
 * The [Stable] annotation ensures Compose can skip recomposition when the view reference
 * hasn't changed.
 *
 * @see ViewProvider for creating views with lifecycle-aware scopes
 * @see VisibilityScopedView for views that track their visibility state
 */
@Stable
interface View {
    /** The composable content to be displayed for this view. */
    val content: @Composable () -> Unit
}

/**
 * Creates a [View] from a composable lambda.
 *
 * @param content The composable function that defines this view's UI.
 * @return A [View] wrapping the provided content.
 */
fun View(content: @Composable () -> Unit): View {
    return object : View {
        override val content: @Composable () -> Unit = content
    }
}
