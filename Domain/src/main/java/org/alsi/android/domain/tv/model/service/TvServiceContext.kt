package org.alsi.android.domain.tv.model.service

import org.alsi.android.domain.user.model.UserAccount
import org.alsi.android.domain.tv.repository.guide.TvDirectoryRepository

/**
 * Created on 7/15/18.
 */
class TvServiceContext (val account: UserAccount, val repository: TvDirectoryRepository, val cursor: TvServiceSession)