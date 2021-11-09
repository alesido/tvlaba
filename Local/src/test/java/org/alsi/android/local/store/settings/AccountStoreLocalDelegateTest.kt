package org.alsi.android.local.store.settings

import io.objectbox.BoxStore
import io.objectbox.DebugFlags
import io.reactivex.observers.TestObserver
import org.alsi.android.local.model.MyObjectBox
import org.alsi.android.local.model.user.UserAccountSubject
import org.alsi.android.local.store.AccountStoreLocalDelegate
import org.alsi.android.local.store.tv.TvChannelLocalStoreDelegateUnitTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.fail

@RunWith(JUnit4::class)
class AccountStoreLocalDelegateTest {

    private lateinit var boxStore: BoxStore

    private lateinit var storeDelegate: AccountStoreLocalDelegate

    @Before
    fun setUp() {
        BoxStore.deleteAllFiles(TEST_DATA_DIRECTORY)

        boxStore = MyObjectBox.builder().directory(TvChannelLocalStoreDelegateUnitTest.TEST_DATA_DIRECTORY)
            .debugFlags(DebugFlags.LOG_QUERIES or DebugFlags.LOG_QUERY_PARAMETERS)
            .build()

        storeDelegate = AccountStoreLocalDelegate(boxStore, UserAccountSubject.create())
    }

    @Test
    fun shouldStoreUserAccount() {
        val testAccount = AccountTestDataFactory.account()
        storeDelegate.addAttachAccount(account = testAccount)
        val readAccount = storeDelegate.attachAccountFor(testAccount.loginName)
        assertEquals(testAccount.loginName, readAccount.loginName)
        assertEquals(testAccount.loginPassword, readAccount.loginPassword)
        assertEquals(testAccount.subscriptions.size, readAccount.subscriptions.size)
        testAccount.subscriptions.forEachIndexed { i, test ->
            with(readAccount.subscriptions[i]) {
                assertEquals(test.serviceId, serviceId)
                assertEquals(test.subscriptionPackage.id, subscriptionPackage.id)
                assertEquals(test.subscriptionPackage.title, subscriptionPackage.title)
                assertEquals(test.subscriptionPackage.termMonths, subscriptionPackage.termMonths)
                assertEquals(test.subscriptionPackage.packets?.size, subscriptionPackage.packets?.size)
                test.subscriptionPackage.packets?.forEachIndexed { j, testPacketName ->
                    assertEquals(testPacketName, subscriptionPackage.packets!![j])
                }
            }
        }
    }

    @Test
    fun shouldUpdateUserAccount() {
        val testAccount = AccountTestDataFactory.account()
        val testAccountUpdate = AccountTestDataFactory.accountUpdate()
        storeDelegate.addAttachAccount(account = testAccount)
        storeDelegate.addAttachAccount(account = testAccountUpdate)
        val readAccount = storeDelegate.attachAccountFor(testAccount.loginName)
        assertEquals(testAccount.loginName, readAccount.loginName)
        assertEquals(testAccount.loginPassword, readAccount.loginPassword)
        assertEquals(testAccount.subscriptions.size, readAccount.subscriptions.size)
        testAccountUpdate.subscriptions.forEachIndexed { i, test ->
            with(readAccount.subscriptions[i]) {
                assertEquals(test.serviceId, serviceId)
                assertEquals(test.subscriptionPackage.id, subscriptionPackage.id)
                assertEquals(test.subscriptionPackage.title, subscriptionPackage.title)
                assertEquals(test.subscriptionPackage.termMonths, subscriptionPackage.termMonths)
                assertEquals(test.subscriptionPackage.packets?.size, subscriptionPackage.packets?.size)
                test.subscriptionPackage.packets?.forEachIndexed { j, testPacketName ->
                    assertEquals(testPacketName, subscriptionPackage.packets!![j])
                }
            }
        }
    }

    @After
    fun tearDown() {
        boxStore.close()
        BoxStore.deleteAllFiles(TvChannelLocalStoreDelegateUnitTest.TEST_DATA_DIRECTORY)
        boxStore.closeThreadResources()
    }

    private fun onTestObserverTermination(testObserver: TestObserver<*>, testTag: String) {
        testObserver.awaitTerminalEvent(1, TimeUnit.SECONDS)
        if (testObserver.errorCount() > 0) {
            testObserver.errors()?.forEach { println("TEST: $testTag"); it.printStackTrace() }
            fail()
        }
    }

    companion object {
        val TEST_DATA_DIRECTORY = File("objectbox/test-db")
    }
}