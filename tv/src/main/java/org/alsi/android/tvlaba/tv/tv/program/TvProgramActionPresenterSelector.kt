package org.alsi.android.tvlaba.tv.tv.program

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.leanback.widget.Action
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.PresenterSelector
import org.alsi.android.tvlaba.R

/**
 * This copy of the original ActionPresenterSelector added just to style button font size.
 * Still, more layout changes are possible.
 */
class TvProgramActionPresenterSelector: PresenterSelector() {

    override fun getPresenter(item: Any?): Presenter = OneLineActionPresenter()

    internal class ActionViewHolder(view: View, layoutDirection: Int) :
        Presenter.ViewHolder(view) {
        var mAction: Action? = null
        var mButton = view.findViewById<View>(R.id.tv_action_button) as Button
        var mLayoutDirection = layoutDirection
    }

    internal abstract class ActionPresenter : Presenter() {
        override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
            val action = item as Action
            val vh = viewHolder as ActionViewHolder
            vh.mAction = action
            val icon = action.icon
            if (icon != null) {
                val startPadding = vh.view.resources
                    .getDimensionPixelSize(R.dimen.lb_action_with_icon_padding_start)
                val endPadding = vh.view.resources
                    .getDimensionPixelSize(R.dimen.lb_action_with_icon_padding_end)
                vh.view.setPaddingRelative(startPadding, 0, endPadding, 0)
            } else {
                val padding = vh.view.resources
                    .getDimensionPixelSize(R.dimen.lb_action_padding_horizontal)
                vh.view.setPaddingRelative(padding, 0, padding, 0)
            }
            if (vh.mLayoutDirection == View.LAYOUT_DIRECTION_RTL) {
                vh.mButton.setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null)
            } else {
                vh.mButton.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null)
            }
        }

        override fun onUnbindViewHolder(viewHolder: ViewHolder) {
            val vh = viewHolder as ActionViewHolder
            vh.mButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            vh.view.setPadding(0, 0, 0, 0)
            vh.mAction = null
        }
    }


    internal class OneLineActionPresenter : ActionPresenter() {
        override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
            val v: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.tv_action_1_line, parent, false)
            return ActionViewHolder(v, parent.layoutDirection)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
            super.onBindViewHolder(viewHolder, item)
            val vh = viewHolder as ActionViewHolder
            val action = item as Action
            vh.mButton.text = action.label1
        }
    }
}