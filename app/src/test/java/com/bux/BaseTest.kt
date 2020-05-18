package com.bux

import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers

/**
 * Base unit test to skip common tasks that may distract from the unit tests.
 */
open class BaseTest {

    init {
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
    }

}