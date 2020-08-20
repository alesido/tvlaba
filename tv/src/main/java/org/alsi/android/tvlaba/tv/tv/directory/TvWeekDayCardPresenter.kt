package org.alsi.android.tvlaba.tv.tv.directory

import android.graphics.Color
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import org.alsi.android.domain.tv.model.guide.TvWeekDay

class TvWeekDayCardPresenter : Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup) = ViewHolder(
            TvWeekDayCardView(parent.context).apply { setBackgroundColor(Color.DKGRAY) }
    )

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val view = viewHolder.view as TvWeekDayCardView
        val data = item as TvWeekDay
        view.monthDayText = data.monthDayString
        view.weekDayText = data.weekDayString
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) = Unit
}