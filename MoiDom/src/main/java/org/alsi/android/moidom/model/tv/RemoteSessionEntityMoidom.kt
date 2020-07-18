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

    /** ID of a database entity to store actual session ID. This isn't remote session ID!
     */
    @Id var entityId: Long,

    /** Login name is a subscriber individual number usually, while there is no any other user
     * ID provided by the service. That is right for Moidom services, as well as for many others.
     * It's a bad idea to add extra ID and maintain its connection to user login name - it adds odd
     * dependencies to Local store and its technology. So, let's use login name as the user ID
     * everywhere in the app, up to the domain level too.
     */
    @Unique var loginName: String,

    /** Session ID created by the remote and used to authorize requests.
     */
    var sessionId: String,

    /** This is Unix timestamp in millis of the last time user logged in to this session.
     */
    var loginTimestampMillis: Long)