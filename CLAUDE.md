# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Development Commands

```bash
# Build entire project
./gradlew build

# Build specific module
./gradlew :library:getbackcompose-core:build
./gradlew :examples:simple-app:sample:app:assembleDebug

# Run tests (foundation module has commonTest)
./gradlew :library:getbackcompose-foundation:allTests
./gradlew :library:getbackcompose-foundation:jvmTest          # JVM only

# Run desktop apps
./gradlew :examples:simple-app:sample:desktop:run
./gradlew :examples:metro-app:sample:desktop:run

# Run wasmJs apps (opens browser)
./gradlew :examples:simple-app:sample:wasmJs:wasmJsBrowserDevelopmentRun
./gradlew :examples:metro-app:sample:wasmJs:wasmJsBrowserDevelopmentRun

# Build iOS frameworks (used by Xcode build phase)
./gradlew :examples:simple-app:sample:ios:embedAndSignAppleFrameworkForXcode
./gradlew :examples:metro-app:sample:ios:embedAndSignAppleFrameworkForXcode

# Compile check for a specific target
./gradlew :examples:metro-app:sample:ios:compileKotlinIosSimulatorArm64
```

iOS apps are built via Xcode projects at `examples/*/sample/iosApp/iosApp.xcodeproj`.

## Architecture

GetBack Compose is a KMP navigation library built on a **four-layer scope model** instead of AndroidX Navigation's lifecycle-based approach:

1. **Render Scope** — Compose's `remember{}` recomposition (not managed by this library)
2. **View Scope** — `CoroutineScope` in `onViewAppear()`, cancels when view is **hidden** (pushed over), restarts when visible again
3. **ViewProvider Scope** — Lives as long as the navigation entry exists, survives being hidden
4. **ManagedCoroutineScope** — Ref-counted hierarchical scopes, cancels when all consumers release

Key difference from AndroidX: scopes cancel on **visibility change**, not Android lifecycle transitions.

### Core Types

- **`ViewProvider`** — Factory that creates a `View` when it becomes visible. Receives a fresh `CoroutineScope` per appearance.
- **`View`** — Stable wrapper around `@Composable` content with a scope reference.
- **`VisibilityScopedView`** — Manages a `CoroutineScope` tied to view visibility. The cross-platform entry point for hosting compose content.
- **`NavigationStack<V>`** — Push/pop navigation. Routes carry full typed objects (not serialized IDs).
- **`ViewSwitcher<K>`** — Tab-like switching. `RetainingScopeViewSwitcher` retains state per tab; `SingleScopeViewSwitcher` shares one scope.
- **`ManagedCoroutineScope`** — Hierarchical scope with `create("childName")` for named children and automatic ref-count cleanup.
- **`Screen`** — Combines `ViewProvider` + `ViewPresentation` (fullscreen or modal).

### Library Modules

| Module | Purpose |
|--------|---------|
| `getbackcompose-foundation` | Coroutine utilities, `ManagedCoroutineScope`, `CoroutineScopeFactory` |
| `getbackcompose-core` | `ViewProvider`, `View`, `VisibilityScopedView`, `ViewContext` |
| `getbackcompose-compose` | `ViewModel` base class, `StateProvider`, state observation, modal surface |
| `getbackcompose-navigation-stack` | `NavigationStack`, `NavigationStackHost`, route factories |
| `getbackcompose-navigation-switcher` | `ViewSwitcher`, `ViewSwitcherHost`, tab retention |
| `getbackcompose-navigation-multidisplay` | Multi-slot navigation (top bar / content / bottom bar) |
| `getbackcompose-lifecycle` | AndroidX Lifecycle integration (Android-only, optional) |
| `getbackcompose-activity` | `ViewModelComponentActivity`, Android Activity integration |
| `getbackcompose-foundation-test` | `TestScopeRule`, `TestFlow`, `FlowMock` (JVM-only) |

All library modules target: Android, Desktop (JVM), iOS (x64/arm64/simulator), JS (IR), WasmJs.
Exceptions: `getbackcompose-lifecycle` and `getbackcompose-activity` are Android-only.

## Example Apps

Three apps demonstrate different DI strategies with identical feature sets:

| App | DI Approach | Platforms |
|-----|-------------|-----------|
| **simple-app** | Manual constructor injection | Android, Desktop, iOS, WasmJs |
| **metro-app** | Metro (KMP DI by Zac Sweers) | Android, Desktop, iOS, WasmJs |
| **dagger-app** | Dagger 2 | Android only |

Shared business logic lives in `examples/shared/sample/core/{auth,data}` — used by both simple-app and metro-app. Platform-specific Ktor engines use `expect/actual` in `PlatformEngine.kt` (Android, Darwin, Java, Js, WasmJs).

### Platform Entry Points

- **Android**: `ViewModelComponentActivity` subclass in `app` module
- **Desktop**: `main()` with `application { Window { VisibilityScopedView(...) } }`
- **iOS**: `MainViewController()` returning `ComposeUIViewController { VisibilityScopedView(...) }`
- **WasmJs**: `main()` with `ComposeViewport(document.body!!) { VisibilityScopedView(...) }`

Non-Android platforms use `ViewContext.EMPTY` (always-foreground context).

## Key Dependencies

- Kotlin 2.3.10, Compose Multiplatform 1.10.2
- Ktor 3.4.1 (HTTP clients per platform)
- Metro 0.11.2 (KMP DI, metro-app only)
- Dagger 2.59.2 (dagger-app only)
- Atomicfu (multiplatform synchronization primitives)
- Coil 3.4.0 (KMP image loading in shared modules)
- Kermit (multiplatform logging)

## Conventions

- Library modules have **zero AndroidX dependencies** in core (Android-specific modules are opt-in)
- Navigation routes are sealed types/enums implementing `ViewKey` — no string-based routing
- Factories pass **full objects**, not serialized primitives or IDs
- `@Volatile` in commonMain uses `@kotlin.concurrent.Volatile` (not `java.lang.Volatile`)
- Thread safety uses atomicfu `SynchronizedObject` + `synchronized(this)` (not JVM `synchronized`)
- Project-wide opt-ins configured in root `build.gradle.kts`: `ExperimentalUuidApi`, `ExperimentalAtomicApi`
- Gradle version catalog (`libs.versions.toml`) with typesafe project accessors
