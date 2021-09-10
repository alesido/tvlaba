package org.alsi.android.local.store.vod

import org.alsi.android.domain.vod.model.guide.directory.VodDirectory
import org.alsi.android.domain.vod.model.guide.directory.VodSection
import org.alsi.android.domain.vod.model.guide.directory.VodUnit

object VodDirectoryTestDataFactory {

    fun directory(): VodDirectory {
        val sections: MutableList<VodSection> = mutableListOf()
        for (i in 1..10) {
            val units: MutableList<VodUnit> = mutableListOf()
            for (j in 1..i) {
                units.add(VodUnit((i * 100 + j).toLong(), i.toLong(), "VOD Unit $i-$j", i * 100))
            }
            sections.add(VodSection(i.toLong(), "VOD Section $i", units))
        }
        return VodDirectory(sections)
    }

    fun directory2(): VodDirectory {
        val sections: MutableList<VodSection> = mutableListOf()
        for (i in 1..8) {
            val units: MutableList<VodUnit> = mutableListOf()
            for (j in 1..i) {
                units.add(VodUnit((i * 100 + j).toLong(), i.toLong(), "VOD Unit $i-$j (2)", i * 100))
            }
            sections.add(VodSection(i.toLong(), "VOD Section $i (2)", units))
        }
        return VodDirectory(sections)
    }
}