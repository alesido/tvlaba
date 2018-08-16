package org.alsi.android.moidom.model

import org.alsi.android.domain.user.model.UserAccount

class LoginEvent(
        val account: UserAccount,
        val data: LoginResponse
)