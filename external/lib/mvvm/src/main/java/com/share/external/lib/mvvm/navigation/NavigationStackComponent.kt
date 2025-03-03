package com.share.external.lib.mvvm.navigation

import androidx.lifecycle.ViewModel
import com.share.external.foundation.coroutines.CoroutineScopeFactory
import com.share.external.lib.mvvm.ComponentCoroutineScope
import com.share.external.lib.mvvm.viewmodel.ViewModelComponent
import dagger.BindsInstance
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

interface NavStackEntryComponent<CS: ComponentCoroutineScope, VM: ViewModel>: ViewModelComponent<VM> {
    val coroutineScope: CS
}

interface NavStackEntryComponentFactory<Component, DynamicDependency: CoroutineScopeDynamicDependency> {
    fun create(@BindsInstance dynamicDependency: DynamicDependency): Component
}

interface CoroutineScopeDynamicDependency {
    val coroutineScopeFactory: CoroutineScopeFactory
    val context: CoroutineContext get() = Dispatchers.IO
}

class DefaultCoroutineScopeDynamicDependency(
    override val coroutineScopeFactory: CoroutineScopeFactory,
    override val context: CoroutineContext = Dispatchers.IO
): CoroutineScopeDynamicDependency
