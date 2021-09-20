package org.alsi.android.presentationvod.model

import org.alsi.android.domain.vod.model.guide.directory.VodDirectory
import org.alsi.android.domain.vod.model.guide.directory.VodDirectoryPosition
import org.alsi.android.domain.vod.model.guide.directory.VodDirectoryUpdateScope

class VodDirectoryBrowseLiveData (
    val directory: VodDirectory,
    val position: VodDirectoryPosition,
    val update: VodDirectoryUpdateScope? = null
)
