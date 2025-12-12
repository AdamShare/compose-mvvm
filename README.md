# GetBack Compose

**Get back** the lifecycle-agnostic behavior that Compose was designed for.

## Why GetBack?

AndroidX lifecycle components were designed for a stateless world—Views that get destroyed and recreated, Activities that die on rotation, Fragments with complex lifecycle callbacks. The workarounds are familiar:

- **Configuration change gymnastics** - `rememberSaveable`, `SavedStateHandle`, process death restoration
- **Stateless ViewModels** - Can't hold references, must reload from repositories
- **Flat dependency scoping** - Everything lives at Activity/Application scope or gets passed as parameters
- **Visibility blindness** - Covered screens stay RESUMED, no way to pause work when hidden
- **String-based navigation** - Runtime route matching, Bundle serialization, type casting

Compose doesn't need these restrictions. It's a **stateful UI framework** where components naturally hold state, survive recomposition, and manage their own lifecycles. GetBack embraces this by providing:

- **Stateful architecture components** - ViewModels and dependencies scoped to navigation hierarchy, not Activity lifecycle
- **Hierarchical dependency scoping** - Child scopes inherit from parents, shared dependencies ref-counted across consumers
- **Visibility-aware lifecycles** - Scopes that cancel when hidden and restart when visible
- **Type-safe navigation** - Full objects passed between screens, compile-time route safety
- **DI framework agnostic** - Works with Dagger, Koin, manual injection, or any DI approach

## Architecture Overview

This architecture provides an alternative to AndroidX Navigation that aligns more closely with how Compose applications think about state and scoping.

### Core Principles

1. **Stateful by default** - Components hold state and dependencies directly, no indirection through stateless abstractions
2. **Visibility-based lifecycles** - Coroutine scopes tied to view visibility rather than Android lifecycle states
3. **Hierarchical scope management** - Child scopes automatically cancel when parents complete
4. **Type-safe navigation** - Navigation keys are interfaces/objects, not string routes
5. **DI agnostic** - Core components have no DI framework dependencies

## Architectural Comparison

### Four-Layer Scope Model

This architecture introduces **four distinct scope layers**, each managed by different concerns. The model is decoupled from AndroidX lifecycle concepts—scopes are based on navigation hierarchy and visibility rather than Activity/Fragment lifecycle states.

```
┌─────────────────────────────────────────────────────────────────────────┐
│  1. RENDER SCOPE (UI Framework)                                         │
│     Managed by: Compose (or any UI framework)                           │
│     Lifecycle: Composition existence                                    │
│     Survives: recomposition                                             │
│     Cancels: composable leaves the UI tree                              │
│     Use for: UI state, remember{}, animations, recomposition            │
├─────────────────────────────────────────────────────────────────────────┤
│  2. VIEW SCOPE (Visibility)                                             │
│     Managed by: onViewAppear / View                                     │
│     Lifecycle: View visibility in the hierarchy                         │
│     Survives: recomposition                                             │
│     Cancels: view hidden (pushed over) or removed                       │
│     Use for: visibility-dependent work, analytics, animations           │
├─────────────────────────────────────────────────────────────────────────┤
│  3. VIEW PROVIDER SCOPE (Navigation Entry)                              │
│     Managed by: NavigationStack, ViewSwitcher                           │
│     Lifecycle: Entry existence in navigation/switcher hierarchy         │
│     Survives: view hidden (pushed over), configuration changes          │
│     Cancels: entry popped from stack or removed from switcher           │
│     Use for: ViewModels, screen-level state, navigation-scoped work     │
├─────────────────────────────────────────────────────────────────────────┤
│  4. MANAGED COROUTINE SCOPE (Dependency Scopes)                         │
│     Managed by: ManagedCoroutineScope (ref-counted)                     │
│     Lifecycle: Reference-counted across consumers                       │
│     Survives: individual ViewProvider lifecycles                        │
│     Cancels: when ref count reaches zero AND all children complete      │
│     Use for: shared repositories, background sync, cross-view state     │
└─────────────────────────────────────────────────────────────────────────┘
```

#### Layer 1: Render Scope (UI Framework)

The UI framework's native composition lifecycle. In Compose, this is the `remember{}` and recomposition model. This layer is **inherent to your UI framework choice**—we don't manage it, we build on top of it.

```kotlin
@Composable
fun HomeContent() {
    // Compose manages this state - survives recomposition,
    // but destroyed when composable leaves the tree
    val scrollState = rememberLazyListState()
}
```

This layer is inherent to the UI framework—we build on top of it rather than managing it.

#### Layer 2: View Scope (Visibility)

The `CoroutineScope` passed to `onViewAppear` is tied to **visibility**. This scope cancels when the view is hidden (e.g., another screen is pushed on top):

```kotlin
class HomeViewProvider : ViewProvider {
    override fun onViewAppear(scope: CoroutineScope): View {
        scope.launch {
            // Only runs while this view is visible
            // Cancels when another screen is pushed on top
            analyticsTracker.trackScreenView("Home")
        }
        return View { HomeContent() }
    }
}
```

Use this scope for work that should only happen while the user can see the screen.

#### Layer 3: View Provider Scope (Navigation Entry)

A `ViewProvider` exists as long as its navigation entry exists. It survives being hidden—when another screen is pushed on top, the ViewProvider remains alive:

```kotlin
class HomeViewProvider(
    scope: ManagedCoroutineScope,  // Survives when hidden
    private val viewModel: HomeViewModel,  // Lives at ViewProvider scope
) : Screen {
    // NavigationStack owned by this ViewProvider
    private val navigationStack = ModalNavigationStack<Screen>(rootScope = scope)

    override fun onViewAppear(scope: CoroutineScope): View {
        // scope cancels when hidden, but viewModel and navigationStack persist
        return View { HomeContent(viewModel, navigationStack) }
    }
}
```

The ViewProvider scope is managed by navigation components (`NavigationStack`, `ViewSwitcher`). When an entry is popped or removed, its ViewProvider scope cancels.

#### Layer 4: Managed Coroutine Scope (Dependency Scopes)

`ManagedCoroutineScope` provides **ref-counted coroutine scopes** that can be shared across multiple ViewProviders. When you create a child scope, the parent tracks it:

```kotlin
// Parent scope (e.g., tab scope)
val tabScope: ManagedCoroutineScope = ...

// Child scopes created from parent (one per ViewProvider)
val homeViewScope = tabScope.create("HomeView")
val detailsViewScope = tabScope.create("DetailsView")

// When tabScope cancels:
// 1. It waits for homeViewScope and detailsViewScope to complete
// 2. Child scopes complete their in-flight work
// 3. Only then does tabScope fully cancel
```

This layer has no AndroidX dependencies. Dependencies at this layer are available for garbage collection when all their child scopes complete and release their references.

### How the Layers Interact

```
Tab Scope (ManagedCoroutineScope)              ← Layer 4: Managed Coroutine
    │
    ├── HomeViewProvider                        ← Layer 3: ViewProvider
    │       └── HomeView                        ← Layer 2: View (visibility)
    │           └── HomeContent (Composable)    ← Layer 1: Render
    │
    └── DetailsViewProvider                     ← Layer 3: ViewProvider
            └── DetailsView                     ← Layer 2: View (visibility)
                └── DetailsContent (Composable) ← Layer 1: Render
```

When navigating from Home → Details:
- **Layer 1 (Render)**: Home's composables leave the tree, Details' composables enter
- **Layer 2 (View)**: Home's View scope cancels (hidden), Details' View scope starts
- **Layer 3 (ViewProvider)**: Both HomeViewProvider and DetailsViewProvider remain active
- **Layer 4 (Coroutine)**: Tab scope continues, both ViewProvider scopes are children of it

When popping back to Home:
- **Layer 1 (Render)**: Details' composables leave, Home's composables re-enter
- **Layer 2 (View)**: Details' View scope cancels, Home's View scope restarts
- **Layer 3 (ViewProvider)**: DetailsViewProvider's scope cancels (popped from stack)
- **Layer 4 (Coroutine)**: Tab scope waits for Details' ViewProvider scope to fully complete

### Comparison to AndroidX Navigation

**AndroidX Navigation** conflates these concepts:
```
Activity CREATED → STARTED → RESUMED → PAUSED → STOPPED → DESTROYED
                        ↓
              NavBackStackEntry follows Activity lifecycle
              (no visibility-based scope - covered screens still RESUMED)
                        ↓
              ViewModel.onCleared() when popped (immediate, no ref-counting)
```

**This Architecture** separates them:
```
Render Scope:         UI framework composition
View Scope:           Visibility-based, cancels when hidden
ViewProvider Scope:   Navigation entry existence, survives being hidden
Coroutine Scope:      Ref-counted dependency scopes
```

#### AndroidX Lifecycle Interop

For migration purposes or when AndroidX lifecycle integration is needed, the `lifecycle` module provides `LifecycleViewScopeProvider`. This wraps navigation destinations with `ViewModelStoreOwner` support, enabling use of AndroidX ViewModels, `SavedStateHandle`, and lifecycle-aware components within the navigation stack:

```kotlin
val viewScopeProviderFactory = ViewScopeProvider.Factory { name, onViewAppear, scope ->
    LifecycleViewScopeProvider(
        name = name,
        onViewAppear = onViewAppear,
        savedState = null, // or restored state
        scope = scope
    )
}
```

This is optional—the core architecture works without AndroidX lifecycle dependencies.

### Key Differences

| Aspect                    | This Architecture                     | AndroidX Navigation                       |
|---------------------------|---------------------------------------|-------------------------------------------|
| **Scope trigger**         | Visibility in composition             | Android lifecycle events                  |
| **Navigation arguments**  | Type-safe factories with any data     | Bundle/SavedStateHandle with type casting |
| **Route definition**      | Kotlin interfaces/objects             | String routes or KClass serialization     |
| **Back stack persistence**| Not automatic (intentional)           | SavedStateHandle survives process death   |
| **Nested navigation**     | Natural via scope hierarchy           | Requires nested NavHosts with coordination|

## Trade-offs Analysis

### Advantages

#### 1. Type-Safe Navigation Arguments
Navigation factories receive strongly-typed dependencies—pass full objects, not just IDs:

```kotlin
// Type-safe at compile time - no string routes or Bundle serialization
navigationStack.push(detailsFactory) { entry ->
    DetailsComponent.Dependency(
        navigationScope = entry,
        feedItem = item,           // Full object, not just an ID
        mediaType = category.mediaType
    )
}
```

With AndroidX Navigation, route mismatches or argument type errors are runtime failures.

#### 2. Hierarchical Scope Cancellation
When a parent scope cancels, all children automatically cancel:

```kotlin
// When Home tab is destroyed, its navigation stack and all pushed screens cancel
ModalNavigationStack(rootScope = homeTabScope)
```

No need for manual cleanup or remembering to cancel coroutines.

#### 3. Visibility-Scoped Operations
Operations respond to actual visibility rather than lifecycle states:

```kotlin
override fun onViewAppear(scope: CoroutineScope): View {
    scope.launch {
        // Only runs while this specific view is visible
        // Cancels when another view is pushed on top
        analyticsTracker.trackScreenView("Details")
    }
    return View { DetailsContent() }
}
```

In AndroidX Navigation, a screen under a modal is still in RESUMED state.

#### 4. Deep Link Stack Construction
Deep links can construct full navigation stacks with proper dependencies at each level:

```kotlin
// Deep link handler can build the entire stack with real dependencies
suspend fun handleDeepLink(uri: Uri) {
    mainViewSwitcher.onSelect(MainViewRoute.LoggedIn)
    homeNavigationStack.push(detailsFactory) { entry ->
        DetailsComponent.Dependency(
            navigationScope = entry,
            feedItem = fetchItem(uri.itemId),
            mediaType = MediaType.APPS
        )
    }
}
```

Each screen receives its full dependencies, not just primitive IDs that require re-fetching.

#### 5. Clean Tab State Retention
`RetainingScopeViewSwitcher` preserves complete tab state (scroll position, nested navigation stacks, form data) without complex configuration:

```kotlin
val tabSwitcher = RetainingScopeViewSwitcher<TabRoute>(scope, defaultKey = TabRoute.Home)
```

### Disadvantages

#### 1. No Automatic Back Stack Persistence

**What's missing:** AndroidX Navigation can save and restore the entire navigation back stack across process death via `SavedStateHandle`.

**Why this may not matter:**
- Many apps intentionally don't restore deep navigation state (users expect fresh start)
- Complex state (network data, form state) often can't be meaningfully restored anyway
- Selective state persistence can still be implemented where valuable

**When this matters:**
- Long forms where users expect to resume exactly where they left off
- Apps targeting low-memory devices with frequent process death

#### 2. Learning Curve

Developers familiar with AndroidX Navigation's convention-based approach need to understand:
- Coroutine scope hierarchies and cancellation
- The visibility lifecycle model vs Android lifecycle

## Roadmap

See [docs/ROADMAP.md](docs/ROADMAP.md) for planned features including:
- Deep link support with hierarchical handling
- Optional back stack persistence
- Navigation stack transition animations
- Multi-slot ViewProviders (top bar / content / bottom bar) for better modal support without requiring separate navigation stacks
- Sample apps demonstrating different DI patterns (Dagger, Koin, manual injection, no DI)

## When to Use This Architecture

### Good Fit
- Apps prioritizing compile-time type safety for navigation
- Apps where navigation state needn't survive process death
- Apps with deep nested navigation (tabs within tabs, modals with stacks)
- Teams wanting explicit control over scope lifecycles

### Consider AndroidX Navigation Instead
- Simple apps with few screens
- Apps requiring back stack persistence across process death
- Rapid prototyping
