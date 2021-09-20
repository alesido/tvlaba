package org.alsi.android.domain.vod.model.guide.directory

import org.alsi.android.domain.vod.model.guide.listing.VodListingWindow


class VodDirectory(
    val sections: List<VodSection>,
    val timeStamp: Long? = null
) {
    val sectionById: Map<Long, VodSection> = sections.map { it.id to it }.toMap()
    val sectionPositionById: Map<Long, Int> = sections.mapIndexed { index, section -> section.id to index}.toMap()

    fun isEmpty(): Boolean = sections.isEmpty()

    companion object {
        fun empty() = VodDirectory(listOf())
    }
}

/**
 * VOD Directory Section
 */
class VodSection(
    val id: Long,
    val title: String? = null,
    val units: List<VodUnit>,

    /**
     *  Some VOD data sources does not provide sections, only units. In such cases
     *  a substitute section introduced to not break VOD Directory structure.
     *  This flag marks such a structure.
     */
    val isSectionSubstitute: Boolean = false
) {
    val unitById: Map<Long, VodUnit> = units.map { it.id to it }.toMap()
    val unitPositionById: Map<Long, Int> = units.mapIndexed { index, unit -> unit.id to index}.toMap()

    fun isEmpty(): Boolean = units.isEmpty()

    @Suppress("MemberVisibilityCanBePrivate")
    companion object {
        const val UNKNOWN_SECTION_ID = -1L

        fun empty() = VodSection(id = UNKNOWN_SECTION_ID, units = listOf())
    }
}

/**
 *  A Unit/Subsection of VOD Directory
 */
open class VodUnit(

    /** Internal ID of VOD Unit. May be the same as correspondent genre ID form a server database.
     */
    val id: Long,

    /** ID of a VOD Section to which this Unit (Subsection) belongs.
     */
    val sectionId: Long? = null,

    /** Title of VOD Unit (Subsection)
     */
    val title: String? = null,

    /** Total items in this Unit. Used for paging. Though, not all VOD API provide it.
     */
    @Suppress("unused") val total: Int? = null,
) {
    var window: VodListingWindow? = null

    fun isEmpty(): Boolean = id == UNKNOWN_UNIT_ID

    @Suppress("MemberVisibilityCanBePrivate")
    companion object {
        const val UNKNOWN_UNIT_ID = -1L

        fun empty() = VodUnit(id = UNKNOWN_UNIT_ID)
    }
}

/**
 * Promotional subtype of VOD Unit
 */
class VodUnitPromo(
    id: Long,
    sectionId: Long?,
    title: String?,
    total: Int?
) : VodUnit(id, sectionId, title, total)