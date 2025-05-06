# Navigation Stack Architecture – Documentation

This document explains the **navigation-stack** implementation that lives under  
`com.share.external.lib.mvvm.navigation.stack`.  
The stack provides a **light-weight, ViewModel–aware alternative** to Jetpack
Navigation that:

* Allows arbitrary **keys** (`NavigationKey`) to identify each screen/route
* Keeps one **ManagedCoroutineScope** per screen for structured concurrency
* Supports **overlay / dialog** display modes alongside full-screen content
* Preserves state across configuration changes with
  **SaveableStateHolder + ViewModelStoreOwner**
* Exposes a **predictive–back-friendly** `BackHandler` hook

---

## Package Overview

```
navigation.stack
├── NavigationBackStack.kt          // Minimal back-stack interface
├── NavigationStack.kt              // Push API + root context
├── NavigationStackEntry.kt         // Per-screen context helpers
├── ViewModelStoreContentProvider.kt// ViewModelStore & owners wrapper
├── ViewModelNavigationStack.kt     // Mutable implementation + state
└── NavigationStackController.kt    // Compose UI integration
```

---

## 1. `NavigationBackStack`

```kotlin
@Stable
interface NavigationBackStack {
    val size: Int
    fun pop(): Boolean
    fun popTo(key: NavigationKey, inclusive: Boolean = false): Boolean
    fun remove(key: NavigationKey)
    fun removeAll()
}
```

* **Purpose** – Smallest contract required for *back* behaviour.  
* **Thread-safety** – Implementations must be **main-thread** only; mutations
  are **not** synchronised.  
* **Error handling** – All mutations are *idempotent*; methods silently return
  `false` if the key cannot be found.

---

## 2. `NavigationStack`

Adds **push** semantics and serves as the *entry point* for navigation from
outside the stack.

```kotlin
interface NavigationStack<V> : NavigationBackStack {
    fun push(key: NavigationKey, content: (NavigationContext<V>) -> V)
}
```

### RootNavigationContext

`RootNavigationContext` is a façade that delegates:

* **CoroutineScope** – via `ManagedCoroutineScope` *by* delegation
* **Back-stack ops** – to the underlying `ViewModelNavigationStack`
* **Push** – wraps user content in a child scope and pushes it.

Use it as the **root dependency** you pass into feature modules:

```kotlin
class MainActivity : ComponentActivity() {
    val rootScope = lifecycle.managedScope()

    val nav = NavigationStackController<ComposableProvider>(
        analyticsId = "main",
        scope = rootScope
    )

    override fun onCreate(...) { … }
}
```

---

## 3. `NavigationStackEntry`

Represents a **single element** in the stack and extends both `NavigationStack`
(to allow nested pushes) and `ManagedCoroutineScope`.

* `remove()` – eliminate current entry, independent of position.  
* `popUpTo()` – convenience wrapper around `popTo`.

`NavigationContext` is the concrete implementation used internally; it is
handed to the content factory so each screen can navigate *relative to itself*.

---

## 4. `ViewModelStoreContentProvider`

```kotlin
@Immutable
open class ViewModelStoreContentProvider<V>(
    val content: V,
    private val scope: ManagedCoroutineScope
) : ManagedCoroutineScope by scope
```

* Wraps a **DefaultViewModelStoreOwner** so every screen gets its own
  `ViewModelStore`.  
* Exposes `LocalOwnersProvider` composable that installs:
  * `ViewModelStoreOwner`
  * `SavedStateRegistryOwner`
  * `SaveableStateHolder`
* `cancel()` clears the store **before** cancelling the scope to prevent memory
  leaks.

---

## 5. `ViewModelNavigationStack`

Concrete, state-driven implementation that backs `NavigationStackController`.

⚙️ **Internals**

| Field | Type | Purpose |
|-------|------|---------|
| `providers` | `DoublyLinkedMap<NavigationKey, ViewModelStoreContentProvider<V>>` | Maintains deterministic order & O(1) removals. |
| `stack` | `State<DoublyLinkedMap<…>>` | `@Composable` state used by controller. |

### Key operations

* **push** – inserts new provider, cancels previous one with same key.
* **pop / popTo / remove** – update map ↦ `updateState()` ↦ Compose re-composes.
* Cancels provider scope **after** removal to allow *predictive back* animation
  to finish.

---

## 6. `NavigationStackController`

High-level **Compose bridge** that turns stack state into UI.

```kotlin
@Composable
fun Content(defaultContent: @Composable () -> Unit)
```

1. **Collects** the current `stack` via `derivedStateOf`.
2. Determines *visible providers*:
   * stops once it hits first `DisplayMode.FullScreen`.
3. Renders:
   * `defaultContent()` if no full-screen provider.
   * One or more overlays via `DialogContainer`.
4. Installs `BackHandler { pop() }` when stack is not empty.
5. Logs human-readable back-stack on every recomposition (Timber).

### DisplayMode

```kotlin
sealed interface DisplayMode {
    object FullScreen : DisplayMode
    data class Overlay(val properties: DialogProperties?) : DisplayMode
}
```

Full-screen is **exclusive**; overlays are rendered *above* previous content.

---

## 7. Typical Usage Flow

```kotlin
// Somewhere in feature A
fun onShowSettings(ctx: NavigationContext<ComposableProvider>) {
    ctx.push(SettingsKey) { innerCtx ->
        ComposableProvider {
            SettingsScreen(
                onBack = { innerCtx.pop() }
            )
        }
    }
}
```

1. `push` creates **child ManagedCoroutineScope**.
2. Child content is shown; previous screen remains in memory.
3. User presses back → `NavigationStackController` receives `BackHandler`
   → `ViewModelNavigationStack.pop()` → child scope is cancelled →
   Compose removes screen.

---

## 8. Threading & Lifecycle Guarantees

* All stack operations occur on **MainImmediate** dispatcher.
* Each **screen scope** inherits its parent; cancelling parent cancels children.
* `ViewModelStoreOwner` is cleared **before** coroutine cancellation to ensure
  any `viewModelScope.launch` blocks are not leaked.

---

## 9. Extension Points

* Provide custom `DisplayMode` to support *bottom-sheets*, *side-panes*, etc.
* Wrap controller with **Predictive Back API 34** transition instead of simple
  `BackHandler`.

---

## 10. Glossary

| Term | Meaning |
|------|---------|
| **NavigationKey** | Immutable descriptor used as map key & analytics ID. |
| **ComposableProvider** | Functional interface producing a screen `@Composable`. |
| **ManagedCoroutineScope** | Custom scope that supports *awaitChildrenComplete*. |

---

### Last updated · May 6 2025
