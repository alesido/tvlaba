package org.alsi.android.tvlaba.mobile.tv.categories

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.tv_category_item.*
import org.alsi.android.presentationtv.model.TvCategoryItemViewModel
import org.alsi.android.tvlaba.R
import javax.inject.Inject

/**
 * Created on 7/7/18.
 */
class TvCategoriesAdapter @Inject constructor() : RecyclerView.Adapter<TvCategoriesAdapter.ViewHolder>()
{
    var items : List<TvCategoryItem> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.tv_category_item, parent, false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    inner class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(item: TvCategoryItem) {
            itemTitleView.text = item.title
            with(item) {
                when {
                    logoDrawableRes != null -> Glide.with(containerView).load(logoDrawableRes).into(itemLogoView)
                    logoRasterUri != null -> Glide.with(containerView).load(logoRasterUri).into(itemLogoView)
                    else -> itemLogoView.setImageDrawable(null)
                }
            }
        }
    }
}