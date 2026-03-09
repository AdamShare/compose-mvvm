package com.getbackcompose.activity

import com.getbackcompose.foundation.coroutines.ManagedCoroutineScope
import com.getbackcompose.activity.application.ApplicationProvider
import kotlinx.coroutines.CoroutineScope

/**
 * Contract for activities that build their view provider from the Application component.
 *
 * This interface defines how an Activity obtains its view provider, typically by creating
 * a Dagger subcomponent from the Application-level component.
 *
 * @param A The Application type that provides dependencies.
 * @param V The ViewProvider type to be created.
 */
interface ActivityViewModelContentProvider<in A, out V> : ApplicationProvider {
    /**
     * Creates the view provider for this activity.
     *
     * This method is called once when the Activity's ViewModel is created, meaning it survives
     * configuration changes but is recreated when the Activity is truly destroyed.
     *
     * @param application The Application instance providing dependencies.
     * @param coroutineScope A scope tied to the ViewModel lifecycle (cancels when Activity finishes).
     * @return The view provider that will render this activity's content.
     */
    fun buildProvider(
        application: A,
        coroutineScope: CoroutineScope
    ): V
}
