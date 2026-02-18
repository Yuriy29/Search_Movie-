# Search Movie

A modern **Android movie browsing application** built with **Jetpack Compose** that allows users to explore top-rated films, search by title

## Features

- **Search** movies by title using the OMDB API
- **Paginated search** with AndroidX Paging 3 for efficient loading of large result sets
- **Movie details**
- **Cache mechanism** via Room database
- **Material 3 Design** with dynamic theming

<img src="screenshots/Recording.gif" alt="App Demo" width="40%">

## How to Run

### Prerequisites

- **Android Studio** Ladybug or newer
- **JDK 17** or higher
- **Android SDK** with API level 35
- **OMDB API Key** (free tier available at [omdbapi.com](https://www.omdbapi.com/apikey.aspx))

### Setup

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd Search_Movie
   ```

2. Open the project in Android Studio

3. The API key is pre-configured in `app/build.gradle.kts`:
   ```kotlin
   buildConfigField("String", "API_KEY", "\"YOUR_API_KEY\"")
   ```
   *(Replace with your own OMDB API key from omdbapi.com)*

### Build & Run

**Using Gradle Wrapper:**
```bash
# Build debug APK
./gradlew assembleDebug

# Run on connected device/emulator
./gradlew installDebug

# Run unit tests
./gradlew test

# Run instrumentation tests
./gradlew connectedAndroidTest
```

**Using Android Studio:**
1. Click **"Run > Run 'app'"** (or press `Shift + F10`)
2. Select a connected device or emulator (API 29+)

## Testing

The project includes comprehensive test coverage with both unit tests and UI tests:

### Unit Tests (`src/test`)

Located in `app/src/test/java/com/dopayurii/movie/`

| Test Class | Description |
|------------|-------------|
| `MovieRepositoryImplTest` | Tests repository logic including cache hits, API fallback, and error handling |
| `SearchMoviesPagingSourceTest` | Tests Paging 3 implementation with various load scenarios |
| `SearchMoviesUseCaseTest` | Validates search use case delegates to repository correctly |
| `FetchMovieDetailsUseCaseTest` | Tests movie details fetching with cache and network scenarios |

**Run unit tests:**
```bash
./gradlew test
```

### UI Tests (`src/androidTest`)

Located in `app/src/androidTest/java/com/dopayurii/movie/`

| Test Class | Description |
|------------|-------------|
| `SearchScreenTest` | Validates search screen UI including search bar, initial state, and query events |
| `DetailsScreenTest` | Tests details screen with loading, error, and content states |

**Run UI tests (requires connected device/emulator):**
```bash
./gradlew connectedAndroidTest
```

### Test Frameworks Used

- **JUnit 4** - Unit testing framework
- **MockK** - Kotlin mocking library for unit tests
- **Coroutines Test** - Testing utilities for Kotlin coroutines
- **Compose Test** - UI testing for Jetpack Compose
- **Espresso** - Android UI testing framework

## Architecture

The project follows **Clean Architecture** with a clear separation of concerns across three layers:

```
app/src/main/java/com/dopayurii/movie/
├── data/               # Data layer
│   ├── local/          # Room entities, DAOs & Paging sources
│   ├── model/          # DTOs for API/Database
│   ├── remote/         # Retrofit API service
│   ├── paging/         # Paging sources for pagination
│   └── repository/     # Repository implementations
├── domain/             # Domain layer (business logic)
│   ├── model/          # Domain models
│   ├── repository/     # Repository interfaces
│   └── usecase/        # Use cases (interactors)
├── presentation/         # Presentation layer (UI)
│   ├── navigation/     # Navigation graph setup
│   └── ui/             # Compose screens, ViewModels, theme
└── di/                 # Dependency injection (Hilt modules)
```

### Key Architectural Decisions

- **MVVM Pattern**: Each screen has a ViewModel that exposes UI state via StateFlow
- **Dependency Injection**: Hilt provides dependencies across all layers
- **Repository Pattern**: Abstracts data sources (API + local database) from domain layer
- **Use Cases**: Encapsulate single business logic operations (e.g., `FetchMovieDetailsUseCase`, `SearchMoviesUseCase`)
- **Unidirectional Data Flow**: UI events → ViewModel → State updates → UI recomposition

## Tech Stack & Libraries

### Core Android
| Library | Version | Purpose |
|---------|---------|---------|
| Kotlin | 2.0.21 | Programming language |
| Android Gradle Plugin | 8.9.1 | Build system |
| Core KTX | 1.10.1 | Kotlin extensions |
| Lifecycle Runtime KTX | 2.6.1 | Lifecycle-aware components |
| SplashScreen | 1.0.1 | App launch screen |

### UI (Jetpack Compose)
| Library | Version | Purpose |
|---------|---------|---------|
| Compose BOM | 2024.09.00 | Compose UI toolkit |
| Material 3 | BOM aligned | Material Design components |
| Activity Compose | 1.8.0 | Compose activity integration |
| ViewModel Compose | 2.8.7 | ViewModel integration |
| Lifecycle Compose | 2.8.7 | Lifecycle-aware composables |
| Navigation Compose | 2.8.9 | Type-safe navigation |
| Coil | 2.6.0 | Async image loading |

### Dependency Injection
| Library | Version | Purpose |
|---------|---------|---------|
| Hilt Android | 2.55 | DI framework |
| Hilt Navigation Compose | 1.2.0 | Hilt + Navigation integration |
| KSP | - | Annotation processor (for Hilt & Room) |

### Networking
| Library | Version | Purpose |
|---------|---------|---------|
| Retrofit | 2.11.0 | HTTP client |
| Gson Converter | 2.11.0 | JSON serialization |
| Logging Interceptor | 4.12.0 | HTTP request/response logging |
| Gson | 2.10.1 | JSON parsing |

### Local Storage
| Library | Version | Purpose |
|---------|---------|---------|
| Room Runtime | 2.6.1 | SQLite ORM |
| Room KTX | 2.6.1 | Kotlin coroutines support |
| Room Compiler | 2.6.1 | Code generation (KSP) |

### Additional
| Library | Version | Purpose |
|---------|---------|---------|
| Coroutines | 1.7.3 | Async programming |
| AndroidX Paging | 3.3.0 | Pagination with runtime and Compose support |
| msz-progress-indicator | 0.8.0 | Animated progress indicators |

### Testing
| Library | Version | Purpose |
|---------|---------|---------|
| JUnit 4 | 4.13.2 | Unit testing |
| AndroidX Test JUnit | 1.1.5 | Android testing extensions |
| Espresso | 3.5.1 | UI testing |
| MockK | 1.13.8 | Mocking framework |
| Coroutines Test | 1.7.3 | Coroutine testing utilities |

## Project Structure

```
MovieExplorer/
├── app/
│   ├── src/main/java/com/dopayurii/movie/
│   │   ├── MainActivity.kt          # App entry point
│   │   ├── MovieApplication.kt      # Hilt application class
│   │   ├── data/
│   │   │   ├── local/               # Room database
│   │   │   ├── model/               # Data transfer objects
│   │   │   ├── paging/              # Paging sources for pagination
│   │   │   ├── remote/              # API service
│   │   │   └── repository/          # Repository implementations
│   │   ├── di/
│   │   │   ├── DatabaseModule.kt    # Database DI
│   │   │   ├── NetworkModule.kt     # Retrofit/OkHttp DI
│   │   │   └── RepositoryModule.kt  # Repository DI
│   │   ├── domain/
│   │   │   ├── model/               # Business models
│   │   │   ├── repository/          # Repository interfaces
│   │   │   └── usecase/             # Business logic
│   │   └── presentation/
│   │       ├── navigation/          # NavHost setup
│   │       └── ui/
│   │           ├── details/         # Details screen & VM
│   │           ├── search/          # Search screen & VM
│   │           └── theme/           # Colors, Theme, Typography
│   └── build.gradle.kts             # App-level dependencies
├── gradle/libs.versions.toml        # Version catalog
└── README.md                        # This file
```

## API Reference

This app uses the [OMDB API](https://www.omdbapi.com/) for movie data.

- **Base URL**: `https://www.omdbapi.com/`
- **Authentication**: API key via query parameter
- **Endpoints Used**:
  - Search: `?s={query}&page={page}`
  - Details: `?i={imdbId}&plot=full`
