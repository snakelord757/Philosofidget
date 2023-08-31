package ru.snakelord.philosofidget.data.datasource.settings

interface WidgetSettingsDataSource {
    suspend fun getAuthorVisibility(): Boolean

    suspend fun setAuthorVisibility(isAuthorVisible: Boolean)

    suspend fun getQuoteLanguage(): String

    suspend fun setQuoteLanguage(language: String)

    suspend fun setQuoteTextSize(quoteTextSize: Float)

    suspend fun getQuoteTextSize(): Float

    suspend fun setQuoteAuthorTextSize(quoteAuthorTextSize: Float)

    suspend fun getQuoteAuthorTextSize(): Float

    suspend fun setWidgetUpdateTime(updateTime: Long)

    suspend fun getWidgetUpdateTime(): Long
}