package org.alsi.android.domain.vod.model.guide.directory

@Suppress("MemberVisibilityCanBePrivate")
class VodDirectoryUpdateScope (

        /** List index of a VOD updated guide section
         */
        val sectionIndex: Int? = null,

        /** List index of a VOD guide unit (subsection) updated, null, if all units of the
         * updated section (section is updated as a whole)
         */
        val unitIndex: Int? = null,

        /**
         * List index of a VOD item in a unit's listing. null, if all items of a unit are updated
         */
        val itemIndex: Int? = null
) {
        fun isSectionUpdate() = sectionIndex != null && null == unitIndex && null == itemIndex
        fun isUnitUpdate() = sectionIndex != null && unitIndex != null && null == itemIndex
        fun isItemUpdate() = sectionIndex != null && unitIndex != null && itemIndex != null

        override fun toString(): String {
                return "($sectionIndex, $unitIndex, $itemIndex)"
        }
}