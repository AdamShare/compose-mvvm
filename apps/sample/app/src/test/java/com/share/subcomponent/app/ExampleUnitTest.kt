package com.share.sample.app

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val linkedHashMap = linkedMapOf(
            "1" to 1,
            "2" to 2,
            "3" to 3,
        )
        assertEquals(linkedHashMap.firstEntry().value, 1)
        assertEquals(linkedHashMap.lastEntry().value, 3)
    }
}