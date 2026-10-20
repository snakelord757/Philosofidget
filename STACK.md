# Стек приложения Philosofidget

Описание составлено по конфигурации Gradle и исходному коду проекта. Версии ниже — объявленные в проекте; разрешение зависимостей и работоспособность сборки в рамках подготовки документа не проверялись.

## Назначение и платформа

Philosofidget — нативное Android-приложение с виджетом рабочего стола, который отображает цитаты сервиса Forismatic. Экран настроек позволяет менять язык цитат (русский или английский), видимость автора, размеры, цвета и выравнивание текста, а также интервал обновления.

| Параметр | Значение |
| --- | --- |
| Основной язык | Kotlin |
| Модуль приложения | `:app` |
| Application ID / namespace | `ru.snakelord.philosofidget` |
| Минимальный Android SDK | 24 |
| Целевой Android SDK | 34 |
| SDK компиляции | 37 |
| Версия приложения | `1.0`, versionCode `1` |
| Совместимость Java и Kotlin JVM target | 17 |

## Сборка

| Инструмент | Версия / настройка |
| --- | --- |
| Gradle Wrapper | 9.8.2 |
| Android Gradle Plugin | 9.2.1 |
| Kotlin Android Gradle Plugin | 2.4.21 |
| Kotlin Serialization Gradle Plugin | 2.4.21 |
| Kotlin Parcelize | Подключён плагин `kotlin-parcelize` |
| Скрипты сборки | Gradle Groovy DSL (`build.gradle`, `settings.gradle`) |
| Репозитории зависимостей | Google Maven, Maven Central, JitPack |
| Репозитории плагинов | Google Maven, Maven Central, Gradle Plugin Portal |

Включены AndroidX и ViewBinding. Встроенная поддержка Kotlin в AGP отключена через `android.builtInKotlin=false`; применяется отдельный Kotlin Android plugin. Для release задано `minifyEnabled false`; конфигурационные файлы ProGuard указаны, но минификация отключена.

## Архитектура

Проект содержит один Gradle-модуль `app`. Разделение на слои выполнено пакетами внутри `app/src/main/java/ru/snakelord/philosofidget`:

- `data` — сетевой сервис, DTO, источники данных и реализации репозиториев; сохранение цитат и настроек.
- `domain` — модели, интерфейсы репозиториев, use case, interactor, фабрика настроек и преобразования данных.
- `presentation` — Activity, Fragment, ViewModel, модели состояния, RecyclerView, DI-конфигурация и компоненты виджета.

Экран настроек использует подход MVVM: `WidgetSettingsViewModel` публикует состояние через `StateFlow`, а `WidgetSettingsFragment` подписывается на него с учётом жизненного цикла. Бизнес-операции вынесены в use case и `WidgetSettingsInteractor`. Это фактическая структура проекта: например, `domain` также содержит Android-зависимый `StringResolverImpl` и сетевой `EscapingCharacterInterceptor`.

## Интерфейс

Интерфейс реализован на Android Views и XML-разметке: AppCompat Activity, Fragment, RecyclerView с несколькими типами ViewHolder, ConstraintLayout и Material-компоненты. Настройки включают переключатели, списки выбора, слайдеры и диалог выбора цвета.

| Библиотека | Объявленная версия | Назначение |
| --- | --- | --- |
| `androidx.core:core-ktx` | 1.19.1 | Kotlin-расширения Android API |
| `androidx.appcompat:appcompat` | 1.8.0 | Базовые компоненты интерфейса и совместимость |
| `com.google.android.material:material` | 1.14.0 | Material-компоненты и темы |
| `androidx.constraintlayout:constraintlayout` | 2.2.2 | XML-разметка с ограничениями |
| `com.github.ab44gl:ColorPicker-Android` | 2.0.1 | Выбор цвета текста |
| `androidx.lifecycle:lifecycle-viewmodel-ktx` | 2.11.0 | ViewModel и `viewModelScope` |
| `androidx.lifecycle:lifecycle-runtime-ktx` | 2.11.0 | `lifecycleScope`, `repeatOnLifecycle` |

ViewBinding фактически используется во Fragment и элементах списка. При этом `AGENTS.md` запрещает его применение в новых изменениях; описание фиксирует существующую реализацию.

## Асинхронность и внедрение зависимостей

- Kotlin Coroutines: `suspend`-функции, `CoroutineScope`, `Dispatchers.IO`, `viewModelScope` и `lifecycleScope`.
- Kotlin Flow / StateFlow: передача состояния экрана и подписки через `repeatOnLifecycle`.
- Koin (`io.insert-koin:koin-android:4.2.2`): регистрация зависимостей через `single`, `factory`, `viewModel`, именованные зависимости и параметры ViewModel.

Koin запускается в `PhilosofidgetApplication`; подключены `commonModule`, `widgetModule` и `widgetSettingsModule`. Coroutines используются через доступные зависимости проекта; отдельная версия `kotlinx-coroutines` в `app/build.gradle` не закреплена.

## Сеть и сериализация

| Библиотека | Объявленная версия | Назначение |
| --- | --- | --- |
| `com.squareup.retrofit2:retrofit` | 3.0.0 | HTTP API с `suspend`-методом |
| `com.squareup.retrofit2:converter-kotlinx-serialization` | 3.0.0 | Преобразование JSON-ответов Retrofit |
| `org.jetbrains.kotlinx:kotlinx-serialization-json` | 1.12.0 | Сериализация DTO и сохранённой цитаты |
| `com.squareup.okhttp3:logging-interceptor` | 5.5.0 | Логирование HTTP-запросов на уровне BASIC |

`OkHttpClient` настроен в Koin-модуле: тайм-аут подключения 10 секунд, `HttpLoggingInterceptor` и собственный `EscapingCharacterInterceptor` для обработки ответа.

Источник цитат — `http://api.forismatic.com/api/1.0/`. `QuoteService` выполняет GET-запрос с параметрами `method=getQuote`, `format=json`, `key` и `lang`. В манифесте есть разрешение `INTERNET`; `network_security_config.xml` разрешает незашифрованный HTTP для `api.forismatic.com` и его поддоменов.

## Локальное хранение

Используется Android `SharedPreferences` с расширением `androidx.core.content.edit`:

- `QUOTE_WIDGET_PREFS` — сохранённая цитата в JSON, кодируемая через Kotlin Serialization.
- `QUOTE_WIDGET_SETTINGS_PREFS` — параметры оформления, языка и обновления виджета в виде примитивов и строк.

Room, SQLite-обёртки и DataStore в текущей реализации не используются.

## Виджет и фоновое обновление

Виджет построен на платформенных `AppWidgetProvider`, `AppWidgetManager` и `RemoteViews`; разметка находится в XML. Основные компоненты — `QuotesWidgetProvider`, `BaseAppWidgetProvider`, `WidgetManagerImpl` и `WidgetViewDelegateImpl`. Для передачи `WidgetPayload` через Intent используется `Parcelable` с `@Parcelize`.

Фоновую загрузку выполняет WorkManager (`androidx.work:work-runtime-ktx:2.12.0`). `QuoteLoadingWorker` наследуется от `CoroutineWorker`: получает цитату, сохраняет её и обновляет виджет. Периодическая задача создаётся через `PeriodicWorkRequestBuilder` с интервалом в часах и ограничениями на наличие сети и достаточный заряд батареи; провайдер регистрирует её через `enqueueUniquePeriodicWork`.

## Тестирование

| Инструмент | Объявленная версия | Назначение |
| --- | --- | --- |
| JUnit (`junit:junit`) | 4.13.2 | Локальные unit-тесты |
| AndroidX Test JUnit (`androidx.test.ext:junit`) | 1.3.0 | Инструментальные тесты Android |
| Espresso (`androidx.test.espresso:espresso-core`) | 3.7.0 | Инфраструктура UI-тестов |

Runner — `androidx.test.runner.AndroidJUnitRunner`.

В исходниках находятся только шаблонные `ExampleUnitTest` (проверка сложения) и `ExampleInstrumentedTest` (проверка package name). Проверок бизнес-логики и UI-сценариев нет. Инструментальный тест ожидает старый идентификатор `ru.snakelord.randomquoteswidget`, тогда как текущий applicationId — `ru.snakelord.philosofidget`.

## Основные источники

- `settings.gradle` — состав модулей и репозитории.
- `build.gradle`, `app/build.gradle`, `gradle.properties`, `gradle/wrapper/gradle-wrapper.properties` — инструменты, версии и параметры сборки.
- `app/src/main/AndroidManifest.xml` — Application, Activity, виджет и сетевые разрешения.
- `app/src/main/java/ru/snakelord/philosofidget/` — реализация слоёв, DI, сети, хранения и UI.
- `app/src/main/res/` — разметка, темы, ресурсы и настройки виджета.
- `app/src/test/`, `app/src/androidTest/` — существующие тесты.
