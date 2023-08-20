package ru.snakelord.philosofidget.domain.string_resolver

import androidx.annotation.StringRes

interface StringResolver {
    fun getString(@StringRes stringResId: Int): String
}