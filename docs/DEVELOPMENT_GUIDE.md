# FanZone Development Guide

This guide is the practical checklist for adding or changing code in FanZone.

## Local Verification

Run these before handing off a change:

```powershell
.\gradlew.bat :app:compileDebugKotlin
.\gradlew.bat :app:testDebugUnitTest
.\gradlew.bat :app:compileDebugAndroidTestKotlin
```

Use instrumented tests on an emulator/device when changing Compose behavior that depends on Android runtime, navigation, or UI interaction.

## Adding A New Feature

Use this structure when a feature has state or more than one screen callback:

```text
feature/newfeature/
├── NewFeatureRoute.kt
├── NewFeatureScreen.kt
├── NewFeatureUiState.kt
└── NewFeatureViewModel.kt
```

### Route

Route composables connect state to navigation.

```kotlin
@Composable
fun NewFeatureRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NewFeatureViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    NewFeatureScreen(
        state = state,
        onBack = onBack,
        onAction = viewModel::onAction,
        modifier = modifier
    )
}
```

### Screen

Screens should be as stateless as possible.

```kotlin
@Composable
fun NewFeatureScreen(
    state: NewFeatureUiState,
    onBack: () -> Unit,
    onAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    // UI only
}
```

Screens should not:

- Create repositories.
- Navigate directly.
- Know route strings.
- Mutate app-level state.

### UiState

Use immutable state.

```kotlin
data class NewFeatureUiState(
    val isLoading: Boolean = false,
    val items: List<Item> = emptyList()
)
```

Prefer derived properties in `UiState` when the logic is simple and UI-facing.

### ViewModel

ViewModels own feature logic.

```kotlin
class NewFeatureViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(NewFeatureUiState())
    val uiState: StateFlow<NewFeatureUiState> = _uiState.asStateFlow()
}
```

Keep Android UI classes out of ViewModels.

## Adding A Route

1. Add the route to `core/navigation/AppDestination.kt`.
2. If it belongs in the bottom bar, add it to `core/navigation/BottomDestination.kt`.
3. Add the composable entry in `core/navigation/FanZoneNavHost.kt`.
4. Pass navigation as callbacks into the feature route/screen.

Route definitions should stay centralized. Do not hardcode route strings inside feature screens.

## Adding Domain Models

Add business models under `domain/model`.

Rules:

- Keep models immutable.
- Avoid Android UI types unless the current app truly requires them. Existing drawable references are acceptable for current mock data, but real API-backed models should avoid UI resource IDs.
- Do not place DTOs or database entities in `domain/model`.

## Adding Repository Behavior

1. Add or update the contract in `domain/repository`.
2. Implement it in `data/repository`.
3. Wire the implementation in `app/AppDependencies.kt`.
4. Add unit tests for mapping, state updates, and edge cases.

Do not let screens call `FakeFanZoneRepository` directly. Go through a ViewModel or app dependency.

## Adding Design System Components

Put reusable Compose components in `core/designsystem/component`.

Choose the file by intent:

- App bars and scaffold pieces: `AppShell.kt`
- Generic headers/avatar/chips: `CommonComponents.kt`
- Event and promotional UI: `PromotionalComponents.kt`
- Ticket/profile/message UI: `TicketDisplayComponents.kt`
- Event detail-only UI: `EventDetailComponents.kt`
- Booking/checkout UI: `BookingComponents.kt`
- Small utility UI: `UtilityComponents.kt`

Guidelines:

- Prefer stateless components.
- Pass data and callbacks in parameters.
- Use theme tokens from `core/designsystem/theme`.
- Keep feature-specific components inside the feature package unless they are reused.

## Testing Expectations

Add JVM unit tests for:

- ViewModel state transitions.
- Formatter/util behavior.
- Repository mapping and filtering.
- Edge cases such as invalid event IDs, empty selections, sold-out states, and payment selection.

Add Compose/instrumented tests for:

- Button callbacks.
- Screen rendering for important states.
- Navigation-facing behavior when a UI flow is risky.

Current examples:

- `core/util/FormattersTest.kt`
- `ui/state/FanZoneViewModelTest.kt`
- `feature/booking/BookingViewModelTest.kt`
- `feature/checkout/CheckoutViewModelTest.kt`
- `feature/tickets/TicketWalletViewModelTest.kt`
- `androidTest/PurchaseSuccessScreenTest.kt`

## Review Checklist

Before merging a change, verify:

- The code compiles.
- Unit tests pass.
- Android test sources compile.
- New feature state lives in a feature ViewModel unless it is truly shared.
- Screens are stateless or close to stateless.
- Domain does not depend on UI/data.
- Core does not depend on feature packages.
- No feature imports another feature's ViewModel.
- Mock data remains behind repository implementations.

## Common Anti-Patterns To Avoid

- Adding more responsibilities to `FanZoneViewModel` without a clear reason.
- Putting all UI helpers into one large component file.
- Importing `FakeFanZoneRepository` from a screen.
- Hardcoding route strings inside features.
- Adding Hilt, Room, Retrofit, or multi-module structure before the app needs them.
- Moving every small UI calculation into a use case.

## Recommended Next Improvements

These are useful future steps, not blockers:

- Move app text into string resources when product copy stabilizes.
- Replace in-memory repository with real data sources.
- Add error/loading/empty states to feature `UiState` objects.
- Add UI previews for important screens and design system components.
- Consider Hilt after adding real repositories/data sources.
