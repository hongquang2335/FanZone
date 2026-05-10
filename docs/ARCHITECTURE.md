# FanZone Architecture

This document explains how the FanZone Android app is organized, why the folders exist, and how future developers should extend it without turning the project back into a prototype-shaped codebase.

## Goals

The architecture optimizes for:

- Clear ownership by feature.
- Small, testable state classes.
- UI code that is easy to preview and reuse.
- Domain and data boundaries that allow fake data today and real API/database implementations later.
- Incremental growth without prematurely moving to a multi-module Gradle setup.

## Package Map

```text
com.example.myapplication
├── app
│   ├── AppDependencies.kt
│   └── FanZoneApp.kt
├── core
│   ├── designsystem
│   │   ├── component
│   │   └── theme
│   ├── navigation
│   └── util
├── data
│   └── repository
├── domain
│   ├── model
│   └── repository
├── feature
│   ├── booking
│   ├── checkout
│   ├── community
│   ├── event
│   ├── home
│   ├── profile
│   ├── success
│   ├── support
│   └── tickets
└── ui
    └── state
```

## Layer Responsibilities

### `app`

Owns application composition.

- `FanZoneApp.kt` creates the `NavController`, renders the app scaffold, and attaches the bottom bar.
- `AppDependencies.kt` is the current manual dependency provider.

Keep this layer thin. It should wire dependencies and top-level UI, not contain feature business logic.

### `core`

Owns reusable infrastructure that can be used by multiple features.

- `core/designsystem/theme`: app colors, typography, and Material theme.
- `core/designsystem/component`: reusable Compose components such as app bars, ticket cards, booking widgets, utility UI, and event detail UI.
- `core/navigation`: route declarations, bottom navigation metadata, and `FanZoneNavHost`.
- `core/util`: framework-light helpers such as formatters.

Core should not depend on feature packages. Features may depend on core.

### `domain`

Owns business-facing contracts and models.

- `domain/model`: immutable models such as `Event`, `TicketTier`, `TicketWalletItem`, `UserProfile`.
- `domain/repository`: interfaces such as `FanZoneRepository`.

Domain should not depend on Compose, Android UI, Navigation, or data implementations.

### `data`

Owns repository implementations.

- `FakeFanZoneRepository` implements `FanZoneRepository` with in-memory mock data.

When real persistence or network data arrives, add implementation classes here, for example `RemoteFanZoneRepository`, `FanZoneApiDataSource`, or `TicketDao`. Keep API/database DTOs out of `domain/model`; map them into domain models.

### `feature`

Owns user-facing flows.

The preferred feature shape is:

```text
feature/booking/
├── BookingRoute.kt
├── BookingScreen.kt
├── BookingUiState.kt
└── BookingViewModel.kt
```

- `Route`: connects ViewModel/state to navigation callbacks.
- `Screen`: stateless Compose UI; receives data and callbacks.
- `UiState`: immutable data required by the screen.
- `ViewModel`: screen or flow logic.

Current examples that follow this shape:

- `feature/booking`
- `feature/checkout`
- `feature/tickets`

Some simpler features still only have `Screen.kt`. That is acceptable until they need local state or business logic.

### `ui/state`

This package currently contains app-level shared state:

- `FanZoneUiState`
- `FanZoneViewModel`

This exists because the app is still demo/in-memory and needs shared state across booking, checkout, success, and wallet. In a production app, this state should gradually move into repositories/use cases so each feature can observe its own data source.

## Dependency Direction

Allowed dependency direction:

```text
app -> core, feature, ui/state
feature -> core, domain
ui/state -> domain, data/app dependencies
data -> domain
core -> domain only when a reusable component needs domain models
domain -> no app/core/data/feature dependency
```

Avoid these:

- `domain` importing Android UI, Compose, Navigation, or data classes.
- `data` importing feature UI classes.
- `core` importing feature classes.
- One feature directly importing another feature's ViewModel.

## Navigation Flow

Navigation is centralized in `core/navigation/FanZoneNavHost.kt`.

Main routes:

```text
home
community
tickets
profile
support
event/{eventId}
booking/{eventId}
checkout/{eventId}
success
```

`AppDestination.kt` owns route definitions. `BottomDestination.kt` owns bottom bar items.

Feature screens should not navigate directly. They expose callbacks such as `onBack`, `onContinue`, or `onOpenEvent`. `FanZoneNavHost` decides what those callbacks do.

## State Ownership

There are two kinds of state:

- App/shared state: selected event, wallet items, current payment method, ticket quantities. Currently in `FanZoneViewModel`.
- Feature-local state: screen-specific selection/filtering. Examples:
  - `BookingViewModel`: booking quantities before commit.
  - `CheckoutViewModel`: selected payment method before confirm.
  - `TicketWalletViewModel`: wallet status filter.

Rule of thumb:

- If state must survive across multiple flows, keep it in a shared source such as repository/app state.
- If state only affects one screen or flow, keep it in that feature's ViewModel.
- Keep `Screen` composables stateless when possible.

## Design System

Reusable UI belongs in `core/designsystem/component`.

Current grouping:

- `AppShell.kt`: top bar, bottom bar, booking footer.
- `CommonComponents.kt`: headers, avatar, chips, categories.
- `PromotionalComponents.kt`: hero, event cards, promotional panels, community cards.
- `TicketDisplayComponents.kt`: ticket card, profile action row, message bubble.
- `EventDetailComponents.kt`: event info, timeline, venue map, notices.
- `BookingComponents.kt`: ticket tiers, quantity stepper, checkout summary, payment method card.
- `UtilityComponents.kt`: empty states, status filter, tags, dividers, metadata rows, format bridge.

Keep components generic enough to reuse, but do not over-abstract. A component that only makes sense in one screen can stay in that feature.

## Data Strategy

The app currently uses `FakeFanZoneRepository`.

Production migration path:

1. Keep `FanZoneRepository` as the domain contract.
2. Add API/database data sources under `data`.
3. Add mappers from DTO/entity objects to domain models.
4. Change `AppDependencies` to provide the real repository.
5. Add tests around repository mapping and failure states.

## Current Trade-Offs

- The project is single-module. This is intentional for speed and simplicity.
- `FanZoneViewModel` is still shared app state. It should shrink as real repositories/use cases appear.
- Text content is mock/demo content and mostly ASCII Vietnamese. Product localization should eventually move to string resources.
- Manual DI is used instead of Hilt. Add Hilt only when dependency graph complexity justifies it.

## When To Move To Multi-Module

Consider multi-module when at least one of these is true:

- Build times become painful.
- Multiple developers need clear ownership boundaries.
- Features need independent testing/release cadence.
- You need stricter dependency enforcement.

Recommended future modules:

```text
:app
:core:designsystem
:core:navigation
:core:util
:domain
:data
:feature:booking
:feature:checkout
:feature:tickets
```
