package org.alsi.android.local.model.settings

import io.objectbox.annotation.Backlink
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToMany

@Entity data class DeviceModelOptionEntity (
        @Id var id: Long,
        var modelId: String) {

    @Backlink
    lateinit var remoteControlKeys: ToMany<RemoteControlKeyEntity>
}