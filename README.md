# TradeMeApp

An Android application that displays the latest listings from the [Trade Me](https://www.trademe.co.nz) marketplace API.

---

## Features

- **Latest Listings feed** — Fetches and displays the most recent Trade Me listings in a scrollable list.
- **Pagination** — Results are loaded in pages of 20 items; scrolling to the bottom of the list automatically loads the next page.
- **Pull-to-refresh** — Swipe down to reload the listing feed from the beginning.
- **Listing details** — Each listing card shows a thumbnail image, title, region, and price information.
- **Auction vs Classified pricing** — Displays Buy Now price for auction listings and a classified price badge for classified listings.
- **Bottom navigation** — Navigates between Latest Listings, Watchlist, and My Trade sections.
- **OAuth 1.0 authentication** — Requests are signed with Trade Me's OAuth 1.0 PLAINTEXT scheme.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM (ViewModel + StateFlow) |
| DI | Hilt |
| Networking | Retrofit + OkHttp |
| JSON Parsing | Gson |
| Image Loading | Coil |
| Async | Kotlin Coroutines |
| Testing | JUnit 4 + kotlinx-coroutines-test |
| Build | Gradle (Kotlin DSL) + KSP |

---

## Architecture

The project follows a clean, feature-based layered architecture:

```
feature/latestlisting/
├── data/
│   ├── service/        # Retrofit API call + raw response models
│   └── repository/     # Repository + domain entities
├── domain/
│   └── interactor/     # Business logic, view-item mapping
└── presentation/
    └── viewmodel/      # ViewModel, UI state, Compose screen
```

- **Service** — Calls the Trade Me REST API and deserialises the JSON response.
- **Repository** — Converts raw responses into domain entities.
- **Interactor** — Applies business rules (e.g. auction vs classified pricing) and maps entities to view items.
- **ViewModel** — Holds the `StateFlow<LatestListingState>` and drives client-side pagination.
- **Screen** — Stateless Compose UI that reacts to the emitted state.

---

## Getting Started

### Prerequisites

- Android Studio Meerkat or later
- JDK 11+
- A Trade Me sandbox / developer account with an OAuth consumer key and secret

### Configuration

1. Copy or create `local.properties` in the project root (it is already git-ignored):

```properties
TRADE_ME_CONSUMER_KEY="your_consumer_key_here"
TRADE_ME_CONSUMER_SECRET="your_consumer_secret_here"
```

These values are injected into `BuildConfig` at compile time and used by the `OAuth1PlaintextInterceptor`.

> **Note:** If the keys are left blank the interceptor will silently skip signing, which is useful for running against a local mock.

### Build & Run

```bash
# Debug build
./gradlew assembleDebug

# Run unit tests
./gradlew test
```

Open the project in Android Studio and run the `app` configuration on an emulator or physical device (API 26+).

---

## Running Tests

Unit tests live under `app/src/test/` and use a fake `ListingsApi` implementation to avoid real network calls.

```bash
./gradlew testDebugUnitTest
```

Key test scenarios covered in `LatestListingViewModelTest`:

| Test | Description |
|---|---|
| Success with valid payload | Parses JSON and emits `Success` state with correct listing data |
| Auction listing | Verifies `buyNowPrice` is populated and `isClassified` is false |
| Classified listing | Verifies `isClassified` is true and `buyNowPrice` is null |
| HTTP error response | Emits `Error` state containing the HTTP status code |
| Network exception | Emits `Error` state with the exception message |
| Page size cap | Initial load is capped to 20 items from a larger result set |
| Load next page | Subsequent page appends remaining items |
| Reload | Resets pagination back to page 1 |

---

## How AI Was Used

[GitHub Copilot](https://github.com/features/copilot) (via the JetBrains IDE extension) was used throughout the development of this project as an AI pair-programmer. Key contributions included:

- **Unit test generation** — The full `LatestListingViewModelTest` suite was written with AI assistance. This included the `FakeListingsApi` sealed class hierarchy, the `MainDispatcherRule` test watcher, and all individual test cases covering success, error, pagination, and reload scenarios.
- **Test scenario design** — AI helped identify edge cases such as the page-size cap, `buyNowPrice` nullability for classified listings, and correct state emission after reload.
- **Boilerplate reduction** — Repetitive wiring code (Hilt module setup, ViewModel `StateFlow` plumbing, Retrofit service scaffolding) was accelerated with AI-generated suggestions.
- **README authoring** — This README was drafted with AI assistance based on the project structure and chat history.

AI-generated code was reviewed and validated by the developer before being committed. All test assertions reflect intended product behaviour and were confirmed to pass against the real implementation.

