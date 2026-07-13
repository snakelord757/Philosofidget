# Available Widget Settings

The settings screen presents a live widget preview and nine controls. The language setting does not alter the static preview quote, but it controls future quotes downloaded from the API.

| Setting | Control | Available values | Default |
| --- | --- | --- | --- |
| Show quote author | Toggle | On or off | On |
| Quote language | Spinner | Russian (`ru`) or English (`en`) | Russian |
| Quote text alignment | Spinner | Start, end, or center | Start |
| Author text alignment | Spinner | Start, end, or center | End |
| Quote text size | Slider | 11–30 sp | 30 sp |
| Author text size | Slider | 11–15 sp | 15 sp |
| Quote refresh interval | Slider | 1–24 hours | 12 hours |
| Quote text color | Color picker | Any color returned by the picker | White |
| Author text color | Color picker | Any color returned by the picker | White |

Slider labels are displayed as rounded whole numbers. The refresh interval is saved as a whole number of hours.

## Applying changes

Each changed control is tracked as a `WidgetPayload`. When settings are saved, the provider can update only the affected visual properties:

- Quote or author text size
- Author visibility
- Quote or author alignment
- Quote or author text color

Language and refresh interval changes affect the WorkManager schedule. Changing the language cancels and recreates the unique periodic task; changing only the interval updates the existing task.

The action button depends on context:

- If widgets already exist, it saves the configuration and broadcasts an update to all widget instances.
- If no widget exists, it saves the configuration and requests home-screen pinning on supported Android 8.0+ launchers.
- When opened by a launcher to configure a specific widget, saving returns that widget ID and closes the activity successfully.

Although the configuration flow receives a target widget ID, the values themselves are global. A setting change applies to every active Philosofidget widget.

