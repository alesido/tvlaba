package org.alsi.android.domain.tv.model.guide

class TvChannelDirectoryPosition (

        /** List index of a category
         *
         */
        val categoryIndex: Int = 0,

        /** List index of a channel in a category given by the index above
         */
        val channelIndex: Int = 0 // index of a channel in the category given by index
)