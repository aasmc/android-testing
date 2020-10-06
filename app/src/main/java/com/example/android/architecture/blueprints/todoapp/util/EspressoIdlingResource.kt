package com.example.android.architecture.blueprints.todoapp.util

import androidx.test.espresso.idling.CountingIdlingResource

object EspressoIdlingResource {

    private const val RESOURCE = "GLOBAL"

    @JvmField
    val countingIdlingResource = CountingIdlingResource(RESOURCE)

    fun increment() {
        countingIdlingResource.increment()
    }

    fun decrement() {
        if (!countingIdlingResource.isIdleNow) {
            countingIdlingResource.decrement()
        }
    }

}

inline fun <T> wrapEspressoIdlingResource(function: () -> T): T {
    // Espresso doesn't work well with coroutines yet
    // see https://github.com/Kotlin/kotlinx.coroutines/issues/982
    EspressoIdlingResource.increment() // set up as busy
    return try {
        function()
    } finally {
        EspressoIdlingResource.decrement() // set up as idle
    }
}