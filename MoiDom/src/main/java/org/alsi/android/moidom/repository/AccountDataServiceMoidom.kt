package org.alsi.android.moidom.repository

import org.alsi.android.data.repository.account.AccountDataGateway
import org.alsi.android.local.store.AccountStoreLocalDelegate
import org.alsi.android.moidom.store.account.AccountServiceRemoteMoidom
import javax.inject.Inject

class AccountDataServiceMoidom @Inject constructor(
        remote: AccountServiceRemoteMoidom,
        local: AccountStoreLocalDelegate)
    : AccountDataGateway(remote, local)