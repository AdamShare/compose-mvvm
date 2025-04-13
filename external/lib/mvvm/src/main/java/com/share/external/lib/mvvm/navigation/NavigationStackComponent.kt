package com.share.external.lib.mvvm.navigation

import com.share.external.foundation.coroutines.ManagedCoroutineScope
import com.share.external.lib.mvvm.navigation.stack.NavigationStack

interface NavStackEntry<Scope: ManagedCoroutineScope, View: ComposableProvider> {
    val coroutineScope: Scope
}

class NavStackEntryViewModel(
    val navStackEntry: NavStackEntry<*, *>,
    val navigationStack: NavigationStack
)

//interface NavStackEntryComponentFactory<Component, DynamicDependency: CoroutineScopeDynamicDependency> {
//    fun create(@BindsInstance dynamicDependency: DynamicDependency): Component
//}
//
//interface CoroutineScopeDynamicDependency {
//    val coroutineScopeFactory: CoroutineScopeFactory
//    val context: CoroutineContext get() = Dispatchers.IO
//}
//
//class DefaultCoroutineScopeDynamicDependency(
//    override val coroutineScopeFactory: CoroutineScopeFactory,
//    override val context: CoroutineContext = Dispatchers.IO
//): CoroutineScopeDynamicDependency
//
//
//class ViewModelComponentScope(
//    managedCoroutineScope: ManagedCoroutineScope,
//) {
//    val viewModelManagedScope = managedCoroutineScope.childManagedScope(
//        name = "ViewModel",
//        context = Dispatchers.Main.immediate
//    )
//}