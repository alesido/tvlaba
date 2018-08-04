package org.alsi.android.moidom.repository

import org.alsi.android.data.repository.account.AccountDataGateway
import org.alsi.android.moidom.store.AccountStoreLocalMoidom
import org.alsi.android.moidom.store.remote.AccountServiceRemoteMoidom
import javax.inject.Inject

class AccountDataServiceMoidom @Inject constructor(
        remote: AccountServiceRemoteMoidom,
        local: AccountStoreLocalMoidom)
    : AccountDataGateway(remote, local)