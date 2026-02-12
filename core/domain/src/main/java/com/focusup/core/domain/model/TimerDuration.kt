package com.focusup.core.domain.model

enum class TimerDuration(val displayName: String, val milliseconds: Long) {
    TEST("5 seconds", 5_000L),
    FIFTEEN_MIN("15 minutes", 15 * 60 * 1000L),
    THIRTY_MIN("30 minutes", 30 * 60 * 1000L),
    ONE_HOUR("1 hour", 60 * 60 * 1000L),
    TWO_HOURS("2 hours", 2 * 60 * 60 * 1000L)
}

