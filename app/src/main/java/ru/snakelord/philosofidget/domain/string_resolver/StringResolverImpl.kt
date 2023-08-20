package ru.snakelord.philosofidget.domain.string_resolver

import android.content.Context
import androidx.annotation.StringRes

class StringResolverImpl(context: Context) : StringResolver {
    private val resources = context.resources

    override fun getString(@StringRes stringResId: Int): String = resources.getString(stringResId)
}