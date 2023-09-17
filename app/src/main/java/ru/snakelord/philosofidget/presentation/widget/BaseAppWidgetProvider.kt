package ru.snakelord.philosofidget.presentation.widget

import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import androidx.annotation.CallSuper
import androidx.core.content.IntentCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import ru.snakelord.philosofidget.presentation.widget.widget_manager.WidgetManager
import ru.snakelord.philosofidget.presentation.widget.widget_manager.WidgetPayload

abstract class BaseAppWidgetProvider : AppWidgetProvider() {
    private val ioScope = CoroutineScope(Dispatchers.IO)
    private val mainScope = MainScope()

    private val _payloads: MutableList<WidgetPayload> = mutableListOf()
    protected val payloads: List<WidgetPayload>
        get() = _payloads

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.getWidgetPayloads()?.let {
            _payloads.clear()
            _payloads.addAll(it)
        }
        super.onReceive(context, intent)
    }

    private fun Intent?.getWidgetPayloads(): Array<WidgetPayload>? = this?.let {
        IntentCompat.getParcelableArrayExtra(it, WidgetManager.WIDGET_PAYLOADS_EXTRA, WidgetPayload::class.java)?.map { payload ->
            payload as WidgetPayload
        }?.toTypedArray()
    }

    @CallSuper
    override fun onDisabled(context: Context) {
        ioScope.cancel(CANCEL_MESSAGE)
        mainScope.cancel(CANCEL_MESSAGE)
        super.onDisabled(context)
    }

    protected fun doOnIo(block: suspend () -> Unit) {
        ioScope.launch { block.invoke() }
    }

    protected fun doOnMain(block: () -> Unit) {
        mainScope.launch { block.invoke() }
    }

    protected fun needFullWidgetUpdate() = _payloads.contains(WidgetPayload.QUOTE)

    private companion object {
        const val CANCEL_MESSAGE = "Job canceled 'cause widget removed"
    }
}