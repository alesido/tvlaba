package org.alsi.android.moidom.repository.tv

import org.alsi.android.datatv.repository.TvBrowseCursorDataRepository
import org.alsi.android.datatv.store.TvBrowseCursorLocalStore
import org.alsi.android.domain.streaming.model.service.StreamingService
import org.alsi.android.moidom.Moidom
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class TvBrowseCursorRepositoryMoiDom @Inject constructor(

        @Named("${Moidom.TAG}.${StreamingService.TV}") localStore: TvBrowseCursorLocalStore

) : TvBrowseCursorDataRepository(localStore)