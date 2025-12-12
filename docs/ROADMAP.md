# Roadmap

## Planned Features

### Deep Link Support

Deep links are not yet implemented but the architecture is designed to support them elegantly.

#### Proposed Approaches

##### Strategy 1: Hierarchical Deep Link Handling (Recommended)

Each view provider handles its portion of the deep link and delegates the remainder to the next level. This naturally builds the full navigation stack with proper dependencies at each level:

```kotlin
interface DeepLinkHandler {
    suspend fun handleDeepLink(uri: Uri): Boolean
}

// Root level: routes to logged-in or onboarding
class MainViewProvider(
    private val viewSwitcher: ViewSwitcher<ActivityViewRoute>,
    private val mainTabFactory: (ManagedCoroutineScope) -> MainTabViewProvider,
) : ViewProvider, DeepLinkHandler {

    override suspend fun handleDeepLink(uri: Uri): Boolean {
        // Ensure user is logged in for content deep links
        if (uri.pathSegments.firstOrNull() in listOf("item", "category")) {
            viewSwitcher.onSelect(ActivityViewRoute.LoggedIn)
            // Delegate to the tab container
            return mainTabProvider?.handleDeepLink(uri) ?: false
        }
        return false
    }
}

// Tab level: selects correct tab and delegates
class MainTabViewProvider(
    private val viewSwitcher: ViewSwitcher<TabRoute>,
    private val homeFactory: (ManagedCoroutineScope) -> HomeViewProvider,
) : ViewProvider, DeepLinkHandler {

    override suspend fun handleDeepLink(uri: Uri): Boolean {
        when (uri.pathSegments.firstOrNull()) {
            "item", "category" -> {
                viewSwitcher.onSelect(TabRoute.Home)
                return homeProvider?.handleDeepLink(uri) ?: false
            }
            "favorites" -> {
                viewSwitcher.onSelect(TabRoute.Favorites)
                return true
            }
        }
        return false
    }
}

// Feature level: pushes screens onto its navigation stack
class HomeViewProvider(
    private val navigationStack: NavigationStack<Screen>,
    private val detailsFactory: (DetailsScreen.Dependencies) -> Screen,
    private val feedRepository: FeedRepository,
) : ViewProvider, DeepLinkHandler {

    override suspend fun handleDeepLink(uri: Uri): Boolean {
        when (uri.pathSegments.firstOrNull()) {
            "item" -> {
                val itemId = uri.getQueryParameter("id") ?: return false
                val item = feedRepository.getItem(itemId)
                navigationStack.push(DetailsScreenKey) { entry ->
                    detailsFactory(DetailsScreen.Dependencies(item, entry))
                }
                return true
            }
            "category" -> {
                val category = uri.getQueryParameter("name") ?: return false
                selectCategory(Category.valueOf(category))
                return true
            }
        }
        return false
    }
}
```

**Pros:**
- Each level only knows about its immediate children
- Full navigation stack builds naturally with proper back behavior
- Each screen receives complete dependencies
- Deep link logic is colocated with navigation logic
- Easy to test each level independently

**Cons:**
- Deep link handling spread across multiple classes
- Requires providers to implement `DeepLinkHandler`

##### Strategy 2: Centralized Deep Link Router

A single handler builds the entire stack in one place:

```kotlin
class DeepLinkRouter(
    private val mainViewSwitcher: ViewSwitcher<ActivityViewRoute>,
    private val getTabSwitcher: () -> ViewSwitcher<TabRoute>?,
    private val getHomeStack: () -> NavigationStack<Screen>?,
    private val detailsFactory: (DetailsScreen.Dependencies) -> Screen,
    private val feedRepository: FeedRepository,
) {
    suspend fun handle(uri: Uri): Boolean {
        return when (uri.pathSegments.firstOrNull()) {
            "item" -> {
                mainViewSwitcher.onSelect(ActivityViewRoute.LoggedIn)
                getTabSwitcher()?.onSelect(TabRoute.Home)

                val item = feedRepository.getItem(uri.getQueryParameter("id")!!)
                getHomeStack()?.push(DetailsScreenKey) { entry ->
                    detailsFactory(DetailsScreen.Dependencies(item, entry))
                }
                true
            }
            else -> false
        }
    }
}
```

**Pros:**
- All deep link logic in one place
- Easier to see full routing at a glance

**Cons:**
- Router needs references to all navigation levels
- Tight coupling to navigation structure
- Harder to maintain as app grows

##### Strategy 3: Direct Screen Navigation

Navigate directly to target without building intermediate stack:

```kotlin
class ItemDeepLinkHandler(
    private val detailsFactory: (DetailsScreen.Dependencies) -> Screen,
) {
    suspend fun handle(uri: Uri, rootScope: NavigationStackScope<Screen>) {
        rootScope.push(DetailsScreenKey) { entry ->
            detailsFactory(
                DetailsScreen.Dependencies(
                    feedItem = LazyFeedItem(itemId = uri.itemId), // Loads own data
                    navigationScope = entry,
                )
            )
        }
    }
}
```

**Pros:**
- Simplest implementation
- Screen is self-contained

**Cons:**
- Back navigation goes to unexpected places
- Screen must handle its own data loading

#### Comparison to AndroidX Deep Links

| Aspect                  | This Architecture                         | AndroidX Navigation                     |
|-------------------------|-------------------------------------------|-----------------------------------------|
| **Definition**          | Kotlin code with full type safety         | String patterns in nav graph or DSL     |
| **Arguments**           | Any object, full dependencies             | Limited to serializable types           |
| **Pre-navigation logic**| Can fetch data, check auth, decide flow   | Limited to argument extraction          |
| **Stack building**      | Explicit control at each level            | Implicit based on graph structure       |
| **Back stack**          | Fully customizable                        | Determined by graph hierarchy           |

---

### Transition Animations

Built-in support for enter/exit animations in `NavigationStackHost` and `ViewSwitcherHost`.

---

### Back Stack Persistence (Optional)

Optional module for apps that need navigation state to survive process death. Would integrate with `SavedStateHandle` while maintaining the architecture's scope model.
