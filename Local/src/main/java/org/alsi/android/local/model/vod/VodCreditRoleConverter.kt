package org.alsi.android.local.model.vod

import io.objectbox.converter.PropertyConverter
import org.alsi.android.domain.streaming.model.VideoStreamKind
import org.alsi.android.domain.vod.model.guide.listing.VodListingItem

class VodCreditRoleConverter: PropertyConverter<VodListingItem.Role, Int> {

    override fun convertToDatabaseValue(role: VodListingItem.Role): Int = role.ordinal

    override fun convertToEntityProperty(databaseValue: Int): VodListingItem.Role =
            if (databaseValue < VideoStreamKind.values().size)
                VodListingItem.Role.values()[databaseValue] else VodListingItem.Role.UNKNOWN
}