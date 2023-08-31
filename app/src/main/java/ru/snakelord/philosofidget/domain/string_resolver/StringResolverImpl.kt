package ru.snakelord.philosofidget.domain.string_resolver

import android.content.res.Resources
import androidx.annotation.StringRes

class StringResolverImpl(private val resources: Resources) : StringResolver {
    override fun getString(@StringRes stringResId: Int): String = resources.getString(stringResId)
}