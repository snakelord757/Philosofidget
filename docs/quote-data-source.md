# Quote Data Source

## Remote source

Quotes are fetched from the public Forismatic API. The Retrofit base URL is:

```text
http://api.forismatic.com/api/
```

The application calls `GET 1.0/` with these query parameters:

| Parameter | Value |
| --- | --- |
| `method` | `getQuote` |
| `format` | `json` |
| `key` | A randomly generated integer |
| `lang` | `ru` or `en`, according to the saved language setting |

The random key is generated in the range `0..999998`. Forismatic uses it to select a quote. The app allows cleartext HTTP only for `api.forismatic.com` through its Android network security configuration.

## Network stack and response processing

Retrofit uses an OkHttp client with:

- A 10-second connection timeout.
- BASIC HTTP logging.
- `EscapingCharacterInterceptor`, which removes the `\'` sequence from response bodies before JSON parsing.
- The Kotlin Serialization Retrofit converter for `application/json`.

The response is decoded into `QuoteDTO`, which contains:

- `quoteText`
- `quoteAuthor`
- `senderName`
- `senderLink`
- `quoteLink`

Only `quoteText` and `quoteAuthor` are mapped into the domain `Quote` model and displayed. Sender and link metadata are currently ignored.

## Loading and caching flow

`QuoteLoadingWorker` performs quote loading as unique periodic WorkManager work named `LOAD_QUOTE_WORKER_NAME`. It requires:

- An active network connection.
- Battery level that is not low.

For every run, the worker:

1. Reads the selected widget language.
2. Generates a random key.
3. Requests a quote from Forismatic.
4. Maps the DTO to a domain quote.
5. Serializes and stores that quote in `SharedPreferences`.
6. Broadcasts a widget update with the `QUOTE` payload.

If any step throws an exception, the worker logs the error and returns `Result.failure()`; no retry policy is requested by the application.

## Local quote cache

The latest quote is stored as a Kotlin Serialization JSON string in the private `SharedPreferences` file `QUOTE_WIDGET_PREFS`, under the key `PREFERENCES_QUOTE_KEY`.

The widget renders this cached value rather than calling the API directly. Only one quote is cached for the entire application, so all active widget instances display the same quote. The cache is deleted when the last widget instance is disabled.

