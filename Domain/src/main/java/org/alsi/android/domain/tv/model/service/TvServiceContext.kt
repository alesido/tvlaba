package org.alsi.android.domain.tv.model.service

import org.alsi.android.domain.user.model.UserAccount
import org.alsi.android.domain.tv.repository.guide.TvGuideRepository

/**
 * Created on 7/15/18.
 */
class TvServiceContext (val account: UserAccount, val repository: TvGuideRepository, val cursor: TvServiceSession)