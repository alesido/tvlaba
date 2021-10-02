package org.alsi.android.moidom.repository.vod

import org.alsi.android.datavod.repository.VodBrowseCursorDataRepository
import org.alsi.android.datavod.repository.VodPlayCursorDataRepository
import org.alsi.android.datavod.store.VodBrowseCursorLocalStore
import org.alsi.android.datavod.store.VodPlayCursorLocalStore
import org.alsi.android.domain.streaming.model.service.StreamingService
import org.alsi.android.moidom.Moidom
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class VodPlayCursorRepositoryMoiDom @Inject constructor(

        @Named("${Moidom.TAG}.${StreamingService.VOD}") localStore: VodPlayCursorLocalStore

) : VodPlayCursorDataRepository(localStore)