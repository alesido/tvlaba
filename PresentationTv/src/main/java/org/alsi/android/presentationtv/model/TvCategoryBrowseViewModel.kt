package org.alsi.android.presentationtv.model

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.observers.DisposableObserver
import org.alsi.android.domain.tv.interactor.guide.TvCategoriesUseCase
import org.alsi.android.domain.tv.model.guide.TvChannelCategory
import org.alsi.android.presentation.state.Resource
import org.alsi.android.presentation.state.ResourceState
import org.alsi.android.presentationtv.mapper.TvCategoryItemViewMapper
import javax.inject.Inject

/** View model to allow user browse channel categories contents in, e.g. Accordion layout.
 *
 * It inherits the categories list presenter responsibilities but interprets them in the
 * MVVMs View Model context:
 *
 * - MVP View interface is substituted with Live Data interface, e.g. liveData.postValue;
 *
 * - MVP presenter interface is replaced by subscription of the Model View to the Live
 * Data object with states.
 *
 * Created on 7/5/18.
 */
open class TvCategoryBrowseViewModel @Inject constructor(
        private val categoriesUseCase: TvCategoriesUseCase)
    : ViewModel()
{
    private val liveData: MutableLiveData<Resource<List<TvCategoryItemViewModel>>> = MutableLiveData()

    private val itemViewMapper = TvCategoryItemViewMapper()

    init {
        fetchChannelCategories()
    }

    fun getLiveData(): LiveData<Resource<List<TvCategoryItemViewModel>>> = liveData

    private fun fetchChannelCategories() {
        liveData.postValue(Resource(ResourceState.LOADING, null, null))
        categoriesUseCase.execute(ChannelCategoriesSubscriber())
    }

    inner class ChannelCategoriesSubscriber: DisposableObserver<List<TvChannelCategory>>() {
        override fun onNext(t: List<TvChannelCategory>) {
            liveData.postValue(Resource(ResourceState.SUCCESS,
                    t.map { itemViewMapper.mapToView(it) }, null))
        }
        override fun onComplete() {
            // seems not applicable
        }
        override fun onError(e: Throwable) {
            liveData.postValue(Resource(ResourceState.ERROR, null, e.localizedMessage))
        }
    }
}