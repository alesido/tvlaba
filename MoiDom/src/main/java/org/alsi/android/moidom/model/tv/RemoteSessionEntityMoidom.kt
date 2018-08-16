package org.alsi.android.moidom.model.tv

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Unique

/** It's possible that one session credentials are used to access a pair of services,
 * e.g. TV and VOD. That's why there is no service ID property.
 *
 */
@Entity
data class RemoteSessionEntityMoidom (

    @Id var entityId: Long,

    @Unique var loginName: String,

    var sessionId: String,

    var startTimestamp: Long)