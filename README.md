# Philosofidget

<div align="center">

<img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher.webp" alt="Philosofidget app icon" width="128" />

**A thoughtful quote on your Android home screen — fresh, simple, and styled by you.**

[![Android](https://img.shields.io/badge/Android-7.0%2B-3DDC84?logo=android&logoColor=white)](https://developer.android.com/about/versions/nougat)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![API](https://img.shields.io/badge/API-24%2B-blue)](https://developer.android.com/tools/releases/platforms)
[![Architecture](https://img.shields.io/badge/Architecture-Clean%20layers-orange)](docs/architecture.md)

Philosofidget is a customizable Android home-screen widget that periodically
displays a random philosophical or motivational quote in English or Russian.

<img src="app/src/main/res/raw/quote_widget_preview.webp" alt="Philosofidget widget preview" width="520" />

</div>

## Why Philosofidget?

Sometimes a small idea at the right moment is enough to change the direction of
your day. Philosofidget keeps that idea within sight without requiring you to
open another app, scroll a feed, or deal with unnecessary distractions.

## Features

- **Random quotes on your home screen** — quotes are fetched from the public
  [Forismatic API](http://forismatic.com/en/api/).
- **English and Russian quotes** — choose the language used for future updates.
- **Automatic refresh** — select an update interval from 1 to 24 hours.
- **Live configuration preview** — see visual changes before saving them.
- **Flexible typography** — configure the quote and author text sizes separately.
- **Independent alignment** — align the quote and author to the start, center, or end.
- **Custom colors** — choose separate colors for the quote and author.
- **Optional author name** — hide it when you prefer a cleaner layout.
- **Resizable widget** — resize it horizontally and vertically to fit your launcher.
- **Offline-friendly rendering** — the latest successfully loaded quote is cached
  locally, so the widget can continue displaying it without an immediate request.
- **Battery-conscious updates** — background work runs only when a network is
  available and the battery is not low.

## Requirements

| Item | Requirement |
| --- | --- |
| Minimum Android version | **Android 7.0 Nougat (API 24)** |
| Target / compile SDK | Android API 34 |
| Widget placement | Android home screen |
| Internet connection | Required to download new quotes |
| Launcher support | A launcher that supports Android app widgets |

> [!NOTE]
> Pinning a widget directly from the app requires Android 8.0 or newer and a
> compatible launcher. On other supported devices, add Philosofidget through
> the launcher's regular widget picker.

## Getting Started

### Add the widget

1. Install and open **Philosofidget**.
2. Choose the quote language, refresh interval, typography, alignment, and colors.
3. Tap the action button to save the configuration and request widget placement.
4. If your launcher cannot add it automatically, long-press an empty area on the
   home screen, open **Widgets**, find **Philosofidget**, and drag it into place.
5. Resize the widget if needed. The first quote will appear after it is downloaded.

To edit an existing widget, use your launcher's widget reconfiguration action
where available, or open the Philosofidget app and save a new configuration.

> [!IMPORTANT]
> Settings and the cached quote are currently shared by every Philosofidget
> widget. If you place several instances, they will all show the same quote and
> use the same appearance.

## Customization

| Setting | Available values | Default |
| --- | --- | --- |
| Show author | On / off | On |
| Quote language | Russian / English | Russian |
| Quote alignment | Start / end / center | Start |
| Author alignment | Start / end / center | End |
| Quote text size | 11–30 sp | 30 sp |
| Author text size | 11–15 sp | 15 sp |
| Refresh interval | 1–24 hours | 12 hours |
| Quote color | Color picker | White |
| Author color | Color picker | White |

Android's WorkManager controls background execution, so the selected interval is
a scheduling preference rather than an exact alarm. Battery optimizations,
connectivity, and system load may delay an update.

## Build from Source

### Prerequisites

- [Android Studio](https://developer.android.com/studio) with Android SDK 34
- JDK 17 or newer (Android Studio's bundled JDK is recommended)
- Git
- An emulator or physical device running Android 7.0+

### Build and install

```bash
git clone https://github.com/snakelord757/Philosofidget.git
cd Philosofidget
```

Open the directory in Android Studio, allow Gradle to sync, select the `app` run
configuration, and run it on a connected device or emulator.

You can also build a debug APK from the command line:

```bash
# macOS / Linux
./gradlew assembleDebug

# Windows
gradlew.bat assembleDebug
```

The generated APK is placed in `app/build/outputs/apk/debug/`.

Run the test suites with:

```bash
# macOS / Linux
./gradlew test connectedAndroidTest

# Windows
gradlew.bat test connectedAndroidTest
```

`connectedAndroidTest` requires a running emulator or connected Android device.

## How It Works

```text
Settings screen
      │
      ├── saves appearance and schedule ──> SharedPreferences
      │
      └── updates preview and widgets

WorkManager ──> Forismatic API ──> local quote cache ──> home-screen widget
```

Philosofidget is a single-module Kotlin application organized into three logical
layers:

- **Presentation** — activity, settings UI, widget rendering, and background worker.
- **Domain** — models, repository contracts, interactors, and focused use cases.
- **Data** — Retrofit networking and SharedPreferences persistence.

Dependency injection is handled by Koin. Retrofit and OkHttp load quotes, Kotlin
Serialization decodes and caches them, and WorkManager schedules periodic updates.
The UI uses Android Views, RecyclerView, Material components, and View Binding.

For a deeper technical tour, see the project documentation:

- [Application overview](docs/application-overview.md)
- [Architecture and runtime flows](docs/architecture.md)
- [Quote data source](docs/quote-data-source.md)
- [Widget settings](docs/widget-settings.md)
- [Settings persistence](docs/widget-settings-persistence.md)
- [Libraries and build dependencies](docs/dependencies.md)

## Privacy and Network Use

Philosofidget requests only the Android `INTERNET` permission. Widget settings and
the latest quote are stored in the app's private local preferences. To obtain a
new quote, the app sends a randomly generated key and the selected language to
the Forismatic service; account creation is not required.

The current Forismatic endpoint uses HTTP rather than HTTPS. Cleartext traffic is
enabled only for the API's domain in the app network security configuration.

## Known Limitations

- All widget instances share one configuration and one cached quote.
- New quotes depend on the availability and content of the third-party
  Forismatic service.
- Background refresh timing is controlled by Android and may not be exact.
- The app interface is currently primarily in Russian, while quote content can be
  requested in Russian or English.

## Contributing

Bug reports, ideas, and pull requests are welcome. Before submitting a change:

1. Create a focused branch from the latest project version.
2. Keep changes small and consistent with the existing layered architecture.
3. Run the relevant unit and instrumentation tests.
4. Describe what changed, why it changed, and how it was verified.

## License

This repository does not currently include a license file. Until a license is
added, no permission is granted to copy, modify, or redistribute the code beyond
what applicable law permits.

---

<div align="center">

**A little philosophy, one home screen at a time.** 💭

</div>
