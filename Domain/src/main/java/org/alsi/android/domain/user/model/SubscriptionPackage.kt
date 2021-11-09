package org.alsi.android.domain.user.model

class SubscriptionPackage (
    val id: Long,
    val title: String? = "Default",
    val termMonths: Int? = 1,
    val packets: List<String>? = listOf()
)