package com.share.subcomponent.app.activity

import com.share.external.lib.mvvm.activity.ActivityComponentProvider
import com.share.external.lib.mvvm.activity.ActivityViewModelComponent
import com.share.external.lib.mvvm.activity.ActivityViewModelComponentProvider
import com.share.external.lib.mvvm.activity.ActivityViewModelCoroutineScope
import com.share.external.lib.mvvm.application.ApplicationCoroutineScope
import com.share.subcomponent.feature.signin.SignInComponent
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import javax.inject.Scope

@Scope
@MustBeDocumented
@Retention(value = AnnotationRetention.RUNTIME)
annotation class ActivityViewModelScope

@ActivityViewModelScope
@Subcomponent(
    modules = [SampleActivityViewModelModule::class]
)
interface SampleActivityViewModelComponent:
    ActivityViewModelComponent,
    ActivityComponentProvider<SampleActivityComponent.Factory>
{
    val navigationController: ActivityViewNavigationController
    val signInComponentFactory: SignInComponent.Factory

    @Subcomponent.Factory
    interface Factory: () -> SampleActivityViewModelComponent

    interface Application {
        val sampleActivityViewModelComponent: Factory
    }

    interface Activity: ActivityViewModelComponentProvider<SampleActivityViewModelComponent> {
        override fun buildViewModelComponent(): SampleActivityViewModelComponent {
            return (getApplication() as Application).sampleActivityViewModelComponent()
        }
    }
}

@Module(
    subcomponents = [
        SampleActivityComponent::class,
        SignInComponent::class
    ]
)
object SampleActivityViewModelModule {
    @Provides
    fun activityViewModelCoroutineScope(
        o: SampleActivityViewModelCoroutineScope
    ): ActivityViewModelCoroutineScope = o

    @Provides
    fun signInParentScope(
        o: SampleActivityViewModelCoroutineScope
    ): SignInComponent.ParentScope = o

    @ActivityViewModelScope
    @Provides
    fun scope(
        applicationCoroutineScope: ApplicationCoroutineScope
    ) = SampleActivityViewModelCoroutineScope(applicationCoroutineScope)

    @ActivityViewModelScope
    @Provides
    fun pageNavigationController(
        scope: SampleActivityViewModelCoroutineScope,
    ) = ActivityViewNavigationController(
        scope = scope.create("ActivityViewNavigationController"),
    )
}

class SampleActivityViewModelCoroutineScope(applicationCoroutineScope: ApplicationCoroutineScope):
    ActivityViewModelCoroutineScope(applicationCoroutineScope),
    SignInComponent.ParentScope

