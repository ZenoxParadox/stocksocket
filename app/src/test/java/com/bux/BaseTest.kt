package com.bux

import io.mockk.mockkClass
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Rule
import org.koin.test.AutoCloseKoinTest
import org.koin.test.mock.MockProviderRule

/**
 * Base unit test to skip common tasks that may distract from the unit tests.
 */
open class BaseTest : AutoCloseKoinTest() {

    init {
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
    }

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        mockkClass(clazz)
    }

}