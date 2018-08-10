package org.alsi.android.moidom.store.tv

import io.reactivex.Completable
import io.reactivex.Single
import org.alsi.android.datatv.store.TvChannelRemoteStore
import org.alsi.android.datatv.store.TvChannelRemoteStoreFeature
import org.alsi.android.domain.tv.model.guide.TvChannel
import org.alsi.android.domain.tv.model.guide.TvChannelCategory
import org.alsi.android.moidom.store.RestServiceMoidom
import javax.inject.Inject

/**
 * Created on 8/6/18.
 */
class TvChannelRemoteStoreMoidom: TvChannelRemoteStore {

    @Inject lateinit var service: RestServiceMoidom

    override fun getCategories(): Single<List<TvChannelCategory>> {
//        service.getGroups()
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getChannels(): Single<List<TvChannel>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getChannels(channelIds: List<Long>): Single<List<TvChannel>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addChannelToFavorites(channelId: Long): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removeChannelFromFavorites(channelId: Long): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun toggleChannelToBeFavorite(channelId: Long): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hasFeature(feature: TvChannelRemoteStoreFeature): Single<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}