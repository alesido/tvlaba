package org.alsi.android.domain.user.model

class UserPreferences (
    var loginRememberMe: Boolean? = false,
    var fontSize: FontSizeOption? = FontSizeOption.MEDIUM
)


enum class FontSizeOption {
    TINY, SMALL, MEDIUM, LARGE, HUGE
}