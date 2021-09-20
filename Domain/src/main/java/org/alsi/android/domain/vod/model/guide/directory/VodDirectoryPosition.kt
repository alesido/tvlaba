package org.alsi.android.domain.vod.model.guide.directory

@Suppress("MemberVisibilityCanBePrivate")
class VodDirectoryPosition (

        /** List index of a VOD guide section
         */
        val sectionIndex: Int = 0,

        /** List index of a VOD guide unit (subsection)
         */
        val unitIndex: Int = 0, // index of a unit in the section given by index

        val itemIndex: Int = 0
) {
        override fun toString(): String {
                return "($sectionIndex, $unitIndex, $itemIndex)"
        }
}