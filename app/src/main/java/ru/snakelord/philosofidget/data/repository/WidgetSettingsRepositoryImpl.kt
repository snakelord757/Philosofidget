package ru.snakelord.philosofidget.data.repository

import ru.snakelord.philosofidget.data.datasource.settings.WidgetSettingsDataSource
import ru.snakelord.philosofidget.domain.factory.WidgetSettingsFactory
import ru.snakelord.philosofidget.domain.model.WidgetSettings
import ru.snakelord.philosofidget.domain.repository.WidgetSettingsRepository

class WidgetSettingsRepositoryImpl(
    private val widgetSettingsDataSource: WidgetSettingsDataSource,
    private val widgetSettingsFactory: WidgetSettingsFactory
) : WidgetSettingsRepository {
    override suspend fun getAuthorVisibility(): Boolean = widgetSettingsDataSource.getAuthorVisibility()

    override suspend fun setAuthorVisibility(isAuthorVisible: Boolean) = widgetSettingsDataSource.setAuthorVisibility(isAuthorVisible)

    override suspend fun getQuoteLanguage(): String = widgetSettingsDataSource.getQuoteLanguage()

    override suspend fun setQuoteLanguage(language: String) = widgetSettingsDataSource.setQuoteLanguage(language)

    override suspend fun setQuoteTextSize(quoteTextSize: Float) = widgetSettingsDataSource.setQuoteTextSize(quoteTextSize)

    override suspend fun getQuoteTextSize(): Float = widgetSettingsDataSource.getQuoteTextSize()

    override suspend fun setQuoteAuthorTextSize(quoteAuthorTextSize: Float) = widgetSettingsDataSource.setQuoteAuthorTextSize(quoteAuthorTextSize)

    override suspend fun getQuoteAuthorTextSize(): Float = widgetSettingsDataSource.getQuoteAuthorTextSize()

    override suspend fun setWidgetUpdateTime(updateTime: Long) = widgetSettingsDataSource.setWidgetUpdateTime(updateTime)

    override suspend fun getWidgetUpdateTime(): Long = widgetSettingsDataSource.getWidgetUpdateTime()

    override suspend fun setQuoteTextGravity(gravity: String) = widgetSettingsDataSource.setQuoteTextGravity(gravity)

    override suspend fun getQuoteTextGravity(): String = widgetSettingsDataSource.getQuoteTextGravity()

    override suspend fun setQuoteAuthorTextGravity(gravity: String) = widgetSettingsDataSource.setQuoteAuthorTextGravity(gravity)

    override suspend fun getQuoteAuthorTextGravity(): String = widgetSettingsDataSource.getQuoteAuthorTextGravity()

    override suspend fun setQuoteTextColor(color: Int) = widgetSettingsDataSource.setQuoteTextColor(color)

    override suspend fun getQuoteTextColor(): Int = widgetSettingsDataSource.getQuoteTextColor()

    override suspend fun setQuoteAuthorTextColor(color: Int) = widgetSettingsDataSource.setQuoteAuthorTextColor(color)

    override suspend fun getQuoteAuthorTextColor(): Int = widgetSettingsDataSource.getQuoteAuthorTextColor()

    override suspend fun getWidgetSettings(): Array<WidgetSettings> = widgetSettingsFactory.getWidgetSettings()

    override suspend fun clearWidgetSettingsParams() = widgetSettingsDataSource.clearWidgetSettingsParams()
}