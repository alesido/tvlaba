package org.alsi.android.domain.streaming.model.options.rc

/** Application-specific remote control functions.
 *
 */

enum class RemoteControlFunction {
    /**
     * Switch to TV application mode
     */
    TV_MODE,

    /**
     * Switch to VOD application mode.
     */
    VOD_MODE,

    /**
     * Video size and aspect ratio.
     */
    ASPECT_RATIO,

    /**
     * Audio/Video/Subtitles/etc. selection.
     */
    TRACK_SELECT,

    /**
     * Rewind, the step is 1 minute by default
     */
    REWIND_LEFT,

    /**
     * Forward, the step is 1 minute by default
     */
    REWIND_RIGHT,

    /**
     * Rewind, the step is 5 minute by default
     */
    REWIND_LEFT_FASTER,

    /**
     * Forward, the step is 5 minute by default
     */
    REWIND_RIGHT_FASTER,

    /**
     * Switch to previous broadcasting day.
     */
    PREVIOUS_DAY,

    /**
     * Switch to next broadcasting day.
     */
    NEXT_DAY,

    /**
     * Switch to the previous program in the schedule.
     */
    PREVIOUS_PROGRAM,

    /**
     * Switch to the next program in the schedule.
     */
    NEXT_PROGRAM,

    /**
     * Add channel to the favorite channels list
     */
    ADD_FAVORITE_CHANNEL,

    /**
     * Delete channel from the favorite channels list
     */
    DELETE_FAVORITE_CHANNEL,

    UNKNOWN
}
