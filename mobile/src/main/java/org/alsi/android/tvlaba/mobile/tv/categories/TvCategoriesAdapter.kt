package org.alsi.android.tvlaba.mobile.tv.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.alsi.android.tvlaba.databinding.TvCategoryItemBinding
import javax.inject.Inject

/**
 * Created on 7/7/18.
 */
class TvCategoriesAdapter @Inject constructor() : RecyclerView.Adapter<TvCategoriesAdapter.ViewHolder>()
{
    var items : List<TvCategoryItem> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = TvCategoryItemBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(itemBinding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    inner class ViewHolder(private val ib: TvCategoryItemBinding) : RecyclerView.ViewHolder(ib.root) {
        fun bind(item: TvCategoryItem) {
            ib.itemTitleView.text = item.title
            with(item) {
                when {
                    logoDrawableRes != null -> Glide.with(ib.root).load(logoDrawableRes).into(ib.itemLogoView)
                    logoRasterUri != null -> Glide.with(ib.root).load(logoRasterUri).into(ib.itemLogoView)
                    else -> ib.itemLogoView.setImageDrawable(null)
                }
            }
        }
    }
}