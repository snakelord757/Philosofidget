# Widget Settings Persistence

## Storage mechanism

Widget settings are stored in Android `SharedPreferences` using the private file:

```text
QUOTE_WIDGET_SETTINGS_PREFS
```

`WidgetSettingsDataSourceImpl` performs the primitive reads and writes. `WidgetSettingsRepositoryImpl` exposes them to the domain layer, while `WidgetSettingsInteractorImpl` converts localized spinner labels into the `Lang` and `TextGravity` enums used by the widget.

Saving is initiated by `WidgetSettingsViewModel`. Changes remain in its in-memory `QuoteWidgetParams` state until the user presses the action button. The interactor then writes all nine values, not only the values that changed.

## Stored keys and types

| Preference key | Type | Default |
| --- | --- | --- |
| `PREFERENCES_IS_AUTHOR_VISIBLE_KEY` | Boolean | `true` |
| `PREFERENCES_QUOTE_LANGUAGE_KEY` | String | Localized Russian option label |
| `PREFERENCES_QUOTE_TEXT_SIZE_KEY` | Float | `30.0` |
| `PREFERENCES_QUOTE_AUTHOR_TEXT_SIZE_KEY` | Float | `15.0` |
| `PREFERENCES_WIDGET_UPDATE_TIME_KEY` | Long | `12` hours |
| `PREFERENCE_QUOTE_TEXT_GRAVITY_KEY` | String | Localized start-alignment label |
| `PREFERENCE_QUOTE_AUTHOR_TEXT_GRAVITY_KEY` | String | Localized end-alignment label |
| `PREFERENCE_QUOTE_TEXT_COLOR_KEY` | Int | `Color.WHITE` (`0xFFFFFFFF`) |
| `PREFERENCE_QUOTE_AUTHOR_TEXT_COLOR_KEY` | Int | `Color.WHITE` (`0xFFFFFFFF`) |

Colors are persisted as Android packed ARGB integers. Language and alignment are persisted as localized display strings rather than stable enum names or language-independent codes. On read, the interactor maps those strings back to enums; unknown language values fall back to Russian and unknown alignment values fall back to start alignment.

## Scope and lifecycle

Preferences are global to the application. No preference key includes an `appWidgetId`, so all widget instances share one configuration.

When the last widget is disabled, `QuotesWidgetProvider.onDisabled()` invokes the settings cleanup use case, which clears the entire `QUOTE_WIDGET_SETTINGS_PREFS` file. The latest cached quote is stored separately in `QUOTE_WIDGET_PREFS` and is removed at the same time.

The `androidx.core.content.edit` extension is used for preference changes. With its default arguments, changes are applied asynchronously through `SharedPreferences.Editor.apply()`.

