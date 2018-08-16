package org.alsi.android.moidom.repository

import org.alsi.android.data.repository.account.AccountDataGateway
import org.alsi.android.local.store.AccountStoreLocalDelegate
import org.alsi.android.moidom.store.account.AccountServiceRemoteMoidom

class AccountDataServiceMoidom: AccountDataGateway(
        AccountServiceRemoteMoidom(),
        AccountStoreLocalDelegate())