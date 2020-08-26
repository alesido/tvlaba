package org.alsi.android.tvlaba.tv.tv.program

import androidx.leanback.widget.AbstractDetailsDescriptionPresenter
import org.alsi.android.domain.tv.model.guide.TvProgramIssue

class TvProgramDetailsDescriptionSimplePresenter: AbstractDetailsDescriptionPresenter() {

    override fun onBindDescription(vh: ViewHolder, item: Any) {
        val program = item as TvProgramIssue
        vh.title.text = program.title // <- possibly time & title
        //vh.subtitle.text <- time & channel
        vh.body.text = program.description
    }
}