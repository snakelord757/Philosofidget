# Kotlin and Android Code Style

This project follows the official Kotlin coding conventions and Android best practices. Prefer consistency with the surrounding code when a rule is not covered here.

## Formatting and Naming

- Use 4 spaces for indentation and never use tabs.
- Keep lines reasonably short; wrap long argument lists and call chains one item per line.
- Use trailing commas in multiline declarations and calls.
- Name classes and objects with `PascalCase`; functions, properties, and local variables with `camelCase`; constants with `UPPER_SNAKE_CASE`.
- Choose names that describe intent. Avoid abbreviations except common Android terms such as `id`, `ui`, and `dto`.
- Keep one main public type per file and give the file the same name as that type.
- Order class members consistently: constants, properties, initialization, public functions, then private helpers.
- Keep visibility as narrow as possible; omit redundant `public` modifiers.

## Kotlin Practices

- Prefer immutable values (`val`) and read-only collections. Use mutation only when it simplifies state ownership.
- Avoid `!!`. Model nullability explicitly and use early returns, safe calls, or `requireNotNull` when absence is a programming error.
- Prefer expressions, collection operations, and small extension functions when they improve readability; avoid clever chains that hide control flow.
- Use data classes for value-like models and sealed types when a state has a fixed set of variants.
- Do not catch `Exception` unless it is rethrown or converted into a meaningful domain result. Never silently ignore failures.
- Add comments only to explain non-obvious decisions or constraints. Public APIs with behavior that is not clear from their signature should use KDoc.

## Android Practices

- Keep Android framework types in the presentation or data layer. Domain logic should remain independent of activities, fragments, views, and contexts where practical.
- Do not store an `Activity`, `Fragment`, or `View` in a long-lived object. Use the application context only when its lifetime is appropriate.
- Keep UI state in a `ViewModel` and expose it as immutable `StateFlow`. Collect flows with lifecycle-aware APIs.
- Run blocking I/O on an injected background dispatcher. Use structured concurrency and never create unmanaged coroutine scopes.
- Use View Binding instead of view lookups. Clear binding references when the corresponding view lifecycle ends.
- Put user-visible text, dimensions, and colors in Android resources. Do not concatenate localized strings in Kotlin.
- Keep activities, fragments, receivers, workers, and adapters thin; delegate business rules to domain collaborators.
- Use WorkManager for deferrable background work and declare appropriate execution constraints.
- Never log secrets, tokens, personal data, or full production payloads.

## Functions and Dependencies

- Keep functions focused on one responsibility. Extract helpers when a block needs its own name to explain its purpose.
- Prefer constructor injection. Avoid service locators and hidden global dependencies in application code.
- Depend on interfaces at layer boundaries when multiple implementations, isolation, or testing justify the abstraction.
- Return domain models or explicit result types across layers instead of leaking network, storage, or UI models.

## Testing and Review

- Add unit tests for business rules, mappings, and state transitions. Add instrumentation tests only for behavior that requires Android APIs or real UI interaction.
- Use descriptive test names that state the condition and expected result.
- Keep tests deterministic: inject dispatchers, clocks, random sources, and external dependencies when they affect behavior.
- Before review, format changed files, remove unused code and imports, and run the relevant unit tests and lint checks.
