package ru.snakelord.philosofidget.data.datasource.settings

interface WidgetSettingsDataSource {
    fun getAuthorVisibility(): Boolean

    fun setAuthorVisibility(isAuthorVisible: Boolean)

    fun getQuoteLanguage(): String

    fun setQuoteLanguage(language: String)
}