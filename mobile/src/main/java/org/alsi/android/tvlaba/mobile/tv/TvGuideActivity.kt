package org.alsi.android.tvlaba.mobile.tv

import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.tv_guide_activity.*
import org.alsi.android.presentation.state.Resource
import org.alsi.android.presentation.state.ResourceState
import org.alsi.android.presentationtv.model.TvCategoryBrowseViewModel
import org.alsi.android.presentationtv.model.TvCategoryItemViewModel
import org.alsi.android.tvlaba.R
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



    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tv_guide_activity)

        browseViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(TvCategoryBrowseViewModel::class.java)

        categoriesListView.layoutManager = LinearLayoutManager(this)
        categoriesListView.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        browseViewModel.getLiveData().observe(this,
                Observer<Resource<List<TvCategoryItemViewModel>>> {
                    if (it != null) handleCategoriesListDataState(it)
        })
    }

    private fun handleCategoriesListDataState(resource: Resource<List<TvCategoryItemViewModel>>) {
        when (resource.status) {
            ResourceState.SUCCESS -> {
                progress.visibility = GONE
                errorMessageView.visibility = GONE
                categoriesListView.visibility = VISIBLE
                setupScreenForSuccess(resource.data?.map { mapper.mapToView(it) })
            }
            ResourceState.LOADING -> {
                progress.visibility = VISIBLE
                errorMessageView.visibility = GONE
                categoriesListView.visibility = GONE
            }
            ResourceState.ERROR -> {
                progress.visibility = GONE
                errorMessageView.visibility = VISIBLE
                errorMessageView.text = resource.message
            }
            else -> {
                progress.visibility = GONE
                errorMessageView.visibility = VISIBLE
                errorMessageView.text = getString(R.string.error_message_unexpected_condition)
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