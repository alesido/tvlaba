package org.alsi.android.tvlaba.mobile.tv

import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.AndroidInjection
import org.alsi.android.presentation.state.Resource
import org.alsi.android.presentation.state.ResourceState
import org.alsi.android.presentationtv.model.TvCategoryBrowseViewModel
import org.alsi.android.presentationtv.model.TvCategoryItemViewModel
import org.alsi.android.tvlaba.R
import org.alsi.android.tvlaba.databinding.TvGuideActivityBinding
import org.alsi.android.tvlaba.mobile.injection.ViewModelFactory
import org.alsi.android.tvlaba.mobile.tv.categories.TvCategoriesAdapter
import org.alsi.android.tvlaba.mobile.tv.categories.TvCategoryItem
import org.alsi.android.tvlaba.mobile.tv.categories.TvCategoryItemViewMapper
import javax.inject.Inject

class TvGuideActivity : AppCompatActivity() {

    private lateinit var browseViewModel : TvCategoryBrowseViewModel

    @Inject lateinit var viewModelFactory: ViewModelFactory
    @Inject lateinit var adapter: TvCategoriesAdapter
    @Inject lateinit var mapper: TvCategoryItemViewMapper

    private lateinit var vb: TvGuideActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        vb = TvGuideActivityBinding.inflate(layoutInflater)
        setContentView(vb.root)

        browseViewModel = ViewModelProvider(this, viewModelFactory)
                .get(TvCategoryBrowseViewModel::class.java)

        vb.categoriesListView.layoutManager = LinearLayoutManager(this)
        vb.categoriesListView.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        browseViewModel.getLiveData().observe(this, {
            if (it != null) handleCategoriesListDataState(it)
        })
    }

    private fun handleCategoriesListDataState(resource: Resource<List<TvCategoryItemViewModel>>) {
        when (resource.status) {
            ResourceState.SUCCESS -> {
                vb.progress.visibility = GONE
                vb.errorMessageView.visibility = GONE
                vb.categoriesListView.visibility = VISIBLE
                setupScreenForSuccess(resource.data?.map { mapper.mapToView(it) })
            }
            ResourceState.LOADING -> {
                vb.progress.visibility = VISIBLE
                vb.errorMessageView.visibility = GONE
                vb.categoriesListView.visibility = GONE
            }
            ResourceState.ERROR -> {
                vb.progress.visibility = GONE
                vb.errorMessageView.visibility = VISIBLE
                vb.errorMessageView.text = resource.message
            }
            else -> {
                vb.progress.visibility = GONE
                vb.errorMessageView.visibility = VISIBLE
                vb.errorMessageView.text = getString(R.string.error_message_unexpected_condition)
            }
        }
    }

    private fun setupScreenForSuccess(categories: List<TvCategoryItem>?) {
        categories?.let {
            adapter.items = it
            adapter.notifyDataSetChanged()
         }
    }
}