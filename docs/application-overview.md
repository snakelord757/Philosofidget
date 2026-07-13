# Application Overview

Philosofidget is an Android home-screen widget application that periodically displays a random philosophical or motivational quote. Quotes can be requested in Russian or English and may include an author.

## Main functionality

- Adds a resizable quote widget to the home screen.
- Fetches random quotes from the Forismatic web API.
- Refreshes the quote on a configurable schedule using WorkManager.
- Keeps the latest quote locally so the widget can render without a direct network request.
- Provides a settings screen with a live visual preview.
- Allows the quote and author appearance to be configured independently.
- Supports launcher-driven widget configuration and, on Android 8.0 or newer, an in-app request to pin the widget when supported by the launcher.

## Typical user flow

1. The user launches the application or opens the widget configuration screen from the launcher.
2. The application shows a preview and the available widget settings.
3. The user adjusts language, typography, alignment, colors, visibility, and refresh interval.
4. The user saves changes for an existing widget or requests that a new widget be added to the home screen.
5. The application periodically downloads a quote, caches it, and refreshes every active widget.

## Widget behavior

The widget displays a progress indicator until a stored quote is available. Once a quote has been loaded, it displays the quote text and, when enabled, its author. Appearance-only changes can be applied through targeted payloads without reloading the quote. A language change causes the quote-loading schedule to be recreated so a quote in the new language can be obtained.

The update task runs only when the device has a network connection and the battery is not low. If loading fails, the current run fails and the previously cached quote remains available.

## Platform requirements

- Minimum Android version: Android 7.0 (API 24)
- Target and compile SDK: API 34
- Widget category: home screen
- Supported resize directions: horizontal and vertical

Settings and the cached quote are application-wide. Multiple widget instances are updated together and do not currently support different per-widget configurations.

