package org.alsi.android.domain.vod.model.guide.directory


class VodDirectory(val sections: List<VodSection>) {
    val sectionById: Map<Long, VodSection> = sections.map { it.id to it}.toMap()
}

/**
 * VOD Directory Section
 */
class VodSection(
    val id: Long,
    val title: String? = null,
    val units: List<VodUnit>
)

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
)

/**
 * Promotional subtype of VOD Unit
 */
class VodUnitPromo(
    id: Long,
    sectionId: Long?,
    title: String?,
    total: Int?
) : VodUnit(id, sectionId, title, total)