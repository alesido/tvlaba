package org.alsi.android.tvlaba.tv.tv.playback

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import android.widget.ImageView
import android.widget.TextView
import androidx.leanback.widget.Action
import androidx.leanback.widget.ControlButtonPresenterSelector
import androidx.leanback.widget.Presenter
import org.alsi.android.tvlaba.R

/** This is a copy of androidx.leanback.widget.ControlButtonPresenterSelector just to add font styling.
 *
 */
class TvControlButtonPresenterSelector : ControlButtonPresenterSelector() {

    private val mPrimaryPresenter: Presenter =
        ControlButtonPresenter(R.layout.tv_control_button_primary)
    private val mSecondaryPresenter: Presenter =
        ControlButtonPresenter(R.layout.tv_control_button_secondary)
    private val mPresenters = arrayOf(mPrimaryPresenter)

    /**
     * Returns the presenter for primary controls.
     */
    override fun getPrimaryPresenter(): Presenter {
        return mPrimaryPresenter
    }

    /**
     * Returns the presenter for secondary controls.
     */
    override fun getSecondaryPresenter(): Presenter {
        return mSecondaryPresenter
    }

    /**
     * Always returns the presenter for primary controls.
     */
    override fun getPresenter(item: Any?): Presenter {
        return mPrimaryPresenter
    }

    internal class ActionViewHolder(view: View) : Presenter.ViewHolder(view) {
        var mIcon: ImageView = view.findViewById<View>(R.id.icon) as ImageView
        var mLabel: TextView? = view.findViewById<View>(R.id.label)?.let { it as TextView }
        var mFocusableView: View = view.findViewById(R.id.button)
    }

    internal class ControlButtonPresenter(private val mLayoutResourceId: Int) :
        Presenter() {
        override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
            val v = LayoutInflater.from(parent.context)
                .inflate(mLayoutResourceId, parent, false)
            return ActionViewHolder(v)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
            val action = item as Action
            val vh = viewHolder as ActionViewHolder
            vh.mIcon.setImageDrawable(action.icon)
            if (vh.mLabel != null) {
                if (action.icon == null) {
                    vh.mLabel!!.text = action.label1
                } else {
                    vh.mLabel!!.text = null
                }
            }
            val contentDescription =
                if (TextUtils.isEmpty(action.label2)) action.label1 else action.label2
            if (!TextUtils.equals(vh.mFocusableView.contentDescription, contentDescription)) {
                vh.mFocusableView.contentDescription = contentDescription
                vh.mFocusableView.sendAccessibilityEvent(
                    AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED
                )
            }
        }

        override fun onUnbindViewHolder(viewHolder: ViewHolder) {
            val vh = viewHolder as ActionViewHolder
            vh.mIcon.setImageDrawable(null)
            if (vh.mLabel != null) {
                vh.mLabel!!.text = null
            }
            vh.mFocusableView.contentDescription = null
        }

        override fun setOnClickListener(
            viewHolder: ViewHolder,
            listener: View.OnClickListener
        ) {
            (viewHolder as ActionViewHolder).mFocusableView.setOnClickListener(listener)
        }
    }
}