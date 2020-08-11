package org.alsi.android.moidom.repository.tv

import org.alsi.android.datatv.repository.TvPlayCursorDataRepository
import org.alsi.android.datatv.store.TvPlayCursorLocalStore
import org.alsi.android.domain.streaming.model.service.StreamingService
import org.alsi.android.moidom.Moidom
import javax.inject.Inject
import javax.inject.Named

class TvPlayCursorRepositoryMoiDom @Inject constructor(

        @Named("${Moidom.TAG}.${StreamingService.TV}") localStore: TvPlayCursorLocalStore

) : TvPlayCursorDataRepository(localStore)