# Project Libraries and Build Dependencies

The project contains one Android application module and resolves artifacts from Google Maven, Maven Central, the Gradle Plugin Portal, and JitPack. The following list reflects dependencies explicitly declared in the Gradle files; transitive dependencies are not enumerated.

## Build plugins

| Plugin | Version | Origin | Purpose |
| --- | --- | --- | --- |
| Android Application plugin | 9.1.1 | Google / Android build tooling | Builds the `app` Android application module. |
| Android Library plugin | 9.1.1 | Google / Android build tooling | Declared at the root but not applied to a module. |
| Kotlin Android plugin | 2.2.10 | JetBrains | Adds Kotlin support for Android. |
| Kotlin Serialization plugin | 1.9.22 | JetBrains | Generates Kotlin Serialization support. |
| Kotlin Parcelize plugin | Version supplied by Kotlin tooling | JetBrains | Generates `Parcelable` implementations such as `WidgetPayload`. |

## Runtime libraries

### AndroidX and Google libraries

These are external libraries maintained by Google/AndroidX rather than application code:

| Library | Version | Purpose |
| --- | --- | --- |
| `androidx.lifecycle:lifecycle-viewmodel-ktx` | 2.6.2 | `ViewModel`, `viewModelScope`, and coroutine integration. |
| `androidx.lifecycle:lifecycle-runtime-ktx` | 2.6.2 | Lifecycle-aware coroutine collection. |
| `androidx.work:work-runtime-ktx` | 2.8.1 | Periodic background quote loading with constraints. |
| `androidx.core:core-ktx` | 1.12.0 | Kotlin extensions for Android APIs, including preferences and intents. |
| `androidx.appcompat:appcompat` | 1.6.1 | AppCompat activity and compatibility support. |
| `com.google.android.material:material` | 1.9.0 | Material widgets, sliders, and UI components. |
| `androidx.constraintlayout:constraintlayout` | 2.1.4 | Constraint-based screen layouts. |

### Other third-party libraries

These libraries are third-party dependencies not maintained by the AndroidX/Google platform team:

| Library | Version | Maintainer/ecosystem | Purpose |
| --- | --- | --- | --- |
| `org.jetbrains.kotlinx:kotlinx-serialization-json` | 1.5.1 | JetBrains / Kotlin | JSON parsing and cached quote serialization. |
| `io.insert-koin:koin-android` | 3.4.3 | InsertKoin | Dependency injection and Android/ViewModel integration. |
| `com.squareup.retrofit2:retrofit` | 2.9.0 | Square | HTTP API interface and request execution. |
| `com.squareup.okhttp3:logging-interceptor` | 4.11.0 | Square | BASIC network request/response logging. |
| `com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter` | 1.0.0 | Jake Wharton | Connects Retrofit responses to Kotlin Serialization. |
| `com.github.ab44gl:ColorPicker-Android` | 2.0 | JitPack-hosted third party | Provides the color picker dialog used by widget settings. |

## Test libraries

| Library | Version | Classification | Purpose |
| --- | --- | --- | --- |
| `junit:junit` | 4.13.2 | Third party | Local JVM unit testing. |
| `androidx.test.ext:junit` | 1.1.5 | AndroidX | JUnit integration for Android instrumentation tests. |
| `androidx.test.espresso:espresso-core` | 3.5.1 | AndroidX | Android UI instrumentation testing. |

## Android platform features used without separate library declarations

The application also relies directly on Android framework APIs such as App Widgets, `RemoteViews`, `SharedPreferences`, activities, fragments, broadcast receivers, and launcher pinning. View Binding is enabled as an Android Gradle Plugin build feature and therefore does not appear as a separate dependency.

