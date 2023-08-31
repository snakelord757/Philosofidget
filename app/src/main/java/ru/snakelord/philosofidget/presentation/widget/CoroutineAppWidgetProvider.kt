package ru.snakelord.philosofidget.presentation.widget

import android.appwidget.AppWidgetProvider
import android.content.Context
import androidx.annotation.CallSuper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

abstract class CoroutineAppWidgetProvider : AppWidgetProvider() {
    protected val ioScope = CoroutineScope(Dispatchers.IO)
    protected val mainScope = MainScope()

    @CallSuper
    override fun onDisabled(context: Context) {
        ioScope.cancel(CANCEL_MESSAGE)
        mainScope.cancel(CANCEL_MESSAGE)
        super.onDisabled(context)
    }

    private companion object {
        const val CANCEL_MESSAGE = "Job canceled 'cause widget removed"
    }
}