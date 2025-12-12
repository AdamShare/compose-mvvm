package com.share.external.lib.view

/**
 * A unique identifier for a view, used for debugging, logging, or analytics purposes.
 *
 * Implement this interface on navigation keys, screen identifiers, or any object that needs
 * a stable human-readable name for tracking view transitions or visibility.
 */
interface ViewKey {
    /** A stable identifier used for tracking screen transitions or visibility. */
    val name: String
}
