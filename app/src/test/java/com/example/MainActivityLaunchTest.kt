package com.example

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MainActivityLaunchTest {
    @Test
    fun testLaunch() {
        Robolectric.buildActivity(MainActivity::class.java).create().start().resume().visible()
    }
}
