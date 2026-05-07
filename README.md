# FanZone

FanZone is an Android app built with Kotlin, Jetpack Compose, Material 3, and Navigation Compose. The app models an event fan community experience: event discovery, booking, checkout, purchase success, ticket wallet, community, profile, and support.

## Developer Docs

- [Architecture](docs/ARCHITECTURE.md): package structure, dependency rules, app flow, state ownership, and current trade-offs.
- [Development Guide](docs/DEVELOPMENT_GUIDE.md): how to add features, screens, routes, state, repositories, design system components, and tests.

## Useful Commands

```powershell
.\gradlew.bat :app:compileDebugKotlin
.\gradlew.bat :app:testDebugUnitTest
.\gradlew.bat :app:compileDebugAndroidTestKotlin
```

## Current Architecture Summary

The codebase follows a pragmatic feature-first architecture:

- `app`: application shell and dependency provider.
- `core`: reusable design system, navigation, and utilities.
- `domain`: business models and repository contracts.
- `data`: repository implementations and data sources.
- `feature`: user-facing flows and screens.
- `ui/state`: temporary app-level state used to coordinate cross-feature demo state.

The project is intentionally still single-module. Move to multi-module only when build time, team ownership, or dependency boundaries justify the extra Gradle complexity.
