package org.alsi.android.local.model.tv

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Index

@Entity
data class TvProgramIssueEntity (

        @Id var id: Long = 0L,

        @Index
        var channelId: Long = 0L,

        @Index
        var startMillis: Long? = null,

        var endMillis: Long? = null,

        /** Ideally, this is a server database ID of a program which we can legally use to
         * check whether the program data are already cached. Still, at least in current server
         * API's, we have no such an ID.
         *
         * Unix time stamp for a program start combined with the channel ID is a proper unique
         * ID to support programs cache.
         *
         * Do not assign value while creating this type of entity or set it to {@link #startMillis}
         * to maintain defined identity scheme.
         *
         * To verify whether a program object is cached query it with both channel ID and
         * program ID equality condition.
         *
         * We can actually go without this property and use {@link #startMillis} instead, but
         * what if there'll be the API-based ID available? Makes sense to keep the property to
         * avoid multiple code changes.
         */
        @Index
        var programId: Long? = startMillis,


        /** Natural schedule ID, the day start in millis (or seconds). It is, particular to clear
         * expired schedule records (Not implemented yet).
         */
        @Index val scheduleId: Long? = null,


        var title: String? = null,

        var description: String? = null
)