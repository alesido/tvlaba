package org.alsi.android.tvlaba.mobile.tv

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import dagger.android.AndroidInjection
import org.alsi.android.presentationtv.model.TvCategoryBrowseViewModel
import org.alsi.android.tvlaba.R
import org.alsi.android.tvlaba.mobile.injection.ViewModelFactory
import org.alsi.android.tvlaba.mobile.tv.categories.TvCategoriesAdapter
import kotlinx.android.synthetic.main.tv_guide_activity.*
import org.alsi.android.presentation.state.Resource
import org.alsi.android.presentation.state.ResourceState
import org.alsi.android.presentationtv.model.TvCategoryItemViewModel
import org.alsi.android.tvlaba.mobile.tv.categories.TvCategoryItem
import org.alsi.android.tvlaba.mobile.tv.categories.TvCategoryItemViewMapper
import javax.inject.Inject

class TvGuideActivity : AppCompatActivity() {

    @Inject lateinit var adapter: TvCategoriesAdapter
    @Inject lateinit var viewModelFactory: ViewModelFactory
    @Inject lateinit var mapper: TvCategoryItemViewMapper
    private lateinit var browseViewModel : TvCategoryBrowseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tv_guide_activity)
        AndroidInjection.inject(this)

        browseViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(TvCategoryBrowseViewModel::class.java)

        categoriesListView.layoutManager = LinearLayoutManager(this)
        categoriesListView.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        browseViewModel.getLiveData().observe(this,
                Observer<Resource<List<TvCategoryItemViewModel>>> {
                    it?.let {
                        handleDataState(it)
                    }
        })
    }

    private fun handleDataState(resource: Resource<List<TvCategoryItemViewModel>>) {
        when (resource.status) {
            ResourceState.SUCCESS -> {
                setupScreenForSuccess(resource.data?.map { mapper.mapToView(it) })
            }
            ResourceState.LOADING -> {
                progress.visibility = View.VISIBLE
                categoriesListView.visibility = View.GONE
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