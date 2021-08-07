package org.alsi.android.tvlaba.tv

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.squareup.rx2.idler.Rx2Idler
import io.reactivex.plugins.RxJavaPlugins
import org.alsi.android.remote.retrofit.error.RetrofitExceptionProducer.RequestMatch.*
import org.alsi.android.tvlaba.R
import org.alsi.android.tvlaba.framework.EventKeyHelper
import org.alsi.android.tvlaba.tv.injection.TestApplicationComponent
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Test how key classified exceptions exposed in the UI
 */
@RunWith(AndroidJUnit4::class)
@LargeTest open class ExceptionPresentationTest {

    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val keyHelper = EventKeyHelper(instrumentation)

    @Rule @JvmField
    val appActivityRule = ActivityScenarioRule(AppActivity::class.java)


    init {
        RxJavaPlugins.setInitComputationSchedulerHandler(
            Rx2Idler.create("RxJava 2.x Computation Scheduler"))

        RxJavaPlugins.setInitIoSchedulerHandler(
            Rx2Idler.create("RxJava 2.x IO Scheduler"))
    }

    @Test
    fun testActionOnSocketTimeout() {

        val app = instrumentation.targetContext.applicationContext as TvVideoStreamingTestApplication
        val xp = (app.component as TestApplicationComponent).retrofitExceptionProducer()

        keyHelper.dpadRight(2)
        keyHelper.dpadDown()

        xp.excludeRequest(LOGIN, CHANNEL_LIST, TV_GROUPS, TV_PROGRAMS)
        xp.start(1)

        keyHelper.dpadCenter()
        //keyHelper.dpadCenter()

        onView(withId(R.id.tvProgramDetailsPrimaryTitle))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}