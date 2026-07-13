# Application Architecture

## Overview

Philosofidget is a single-module Android application (`:app`) written in Kotlin. Its source code follows a layered structure inspired by Clean Architecture:

```text
presentation -> domain <- data
```

Dependency direction is enforced mainly through interfaces in the domain layer. The presentation layer coordinates Android UI and widget lifecycle events, while the data layer implements persistence and network access.

## Layers

### Presentation layer

Package: `ru.snakelord.philosofidget.presentation`

Main responsibilities:

- Starts dependency injection in `PhilosofidgetApplication`.
- Hosts the settings screen in `MainActivity` and `WidgetSettingsFragment`.
- Holds settings-screen state in `WidgetSettingsViewModel` using `StateFlow`.
- Renders the home-screen widget through `QuotesWidgetProvider`, `WidgetViewDelegate`, and Android `RemoteViews`.
- Sends targeted widget updates through `WidgetManager` and `WidgetPayload`.
- Loads quotes periodically with `QuoteLoadingWorker` and WorkManager.

The settings UI uses a RecyclerView with separate view holders for toggles, spinners, sliders, and color pickers. View Binding is enabled for screen and list-item views.

### Domain layer

Package: `ru.snakelord.philosofidget.domain`

Main responsibilities:

- Defines core models such as `Quote`, `QuoteWidgetParams`, `WidgetSettings`, `Lang`, and `TextGravity`.
- Defines repository and factory interfaces.
- Encapsulates quote operations in small use cases: obtaining a random key, loading a quote, caching it, reading/removing the cached quote, reading the update interval, and clearing settings.
- Coordinates settings conversion and persistence through `WidgetSettingsInteractor`.
- Maps network DTOs to the smaller domain `Quote` model.

The domain layer contains some Android-aware types and helpers, such as color annotations, `Color`, lifecycle extensions, and a string resource resolver. It is therefore a logical separation rather than a fully platform-independent module.

### Data layer

Package: `ru.snakelord.philosofidget.data`

Main responsibilities:

- Calls the Forismatic API through Retrofit and OkHttp.
- Serializes and deserializes cached quotes with Kotlin Serialization.
- Stores both quote data and widget settings in `SharedPreferences`.
- Implements the domain repository interfaces.
- Builds the list of settings shown by the settings screen, including defaults and allowed ranges.

## Dependency Injection

Koin wires the application in three modules:

- `commonModule`: resource string resolution, the IO dispatcher, and `WidgetManager`.
- `widgetModule`: networking, quote storage, quote repositories/use cases, mapping, and widget rendering.
- `widgetSettingsModule`: settings storage, repository, interactor, factory, and parameterized `WidgetSettingsViewModel` creation.

Most stateless collaborators are factories. Shared preferences, the widget view delegate, string resolver, and widget manager are singletons.

## Main Runtime Flows

### Settings flow

1. `MainActivity` opens `WidgetSettingsFragment`, optionally with a widget ID supplied by the launcher.
2. `WidgetSettingsViewModel` reads the persisted settings through `WidgetSettingsInteractor`.
3. The fragment renders a live preview and a RecyclerView of controls.
4. User changes update an in-memory `QuoteWidgetParams` state and add a matching `WidgetPayload`.
5. The save/add action persists all settings and either broadcasts a widget update or asks the launcher to pin a widget.
6. In launcher configuration mode, the activity returns the configured widget ID with `RESULT_OK` after saving.

### Quote update flow

1. `QuotesWidgetProvider` schedules unique periodic work when the first widget is enabled and no cached quote exists.
2. `QuoteLoadingWorker` waits for a connected network and a non-low battery state.
3. The worker generates a random key, requests a quote in the configured language, and stores the mapped quote.
4. `WidgetManager` broadcasts a quote-only update.
5. `QuotesWidgetProvider` reads the cached quote and current settings, maps them to `WidgetState`, and updates the `RemoteViews`.

Changing the language restarts the periodic work; changing the update interval updates the existing unique periodic work. When the last widget is disabled, the worker is cancelled and the cached quote and widget settings are cleared.

## Android Entry Points

- Application: `PhilosofidgetApplication`
- Launcher/configuration activity: `MainActivity`
- App widget receiver: `QuotesWidgetProvider`
- Background worker: `QuoteLoadingWorker`

The widget supports home-screen placement and horizontal/vertical resizing. On Android 12 and newer it is also declared reconfigurable and supplies a preview layout.

