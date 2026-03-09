package com.share.sample.feature.home

import com.getbackcompose.foundation.coroutines.ManagedCoroutineScope
import com.getbackcompose.navigation.switcher.RetainingScopeViewSwitcher
import com.getbackcompose.navigation.switcher.ViewSwitcher
import com.share.sample.core.data.model.Category

/**
 * ViewSwitcher for category-based feed navigation.
 *
 * Uses [RetainingScopeViewSwitcher] to preserve each category's view state
 * (scroll position, loaded data, etc.) when switching between categories.
 */
class HomeCategoryViewSwitcher(scope: ManagedCoroutineScope) :
    ViewSwitcher<Category> by RetainingScopeViewSwitcher(
        scope = scope,
        defaultKey = Category.default
    )
