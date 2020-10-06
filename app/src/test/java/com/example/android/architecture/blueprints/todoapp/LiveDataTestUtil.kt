package com.example.android.architecture.blueprints.todoapp

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * This function observes the liveData until it receives a new value (via onChanged)
 * Then it removes the observer. If the liveData already has a value it returns it immediately.
 * Additionally if the value is never set, it will throw an exception after 2 seconds of waiting
 * (or whatever is passed as the parameter [time]. This prevents tests that never finish when something
 * goes wrong.
 * @param time time of waiting before an exception is thrown
 * @param timeUnit time unit for the specified above time
 * @param afterObserve function invoked after the value has been received
 * @return new data that was passed to the LiveData object
 */
@VisibleForTesting(otherwise = VisibleForTesting.NONE)
fun <T> LiveData<T>.getOrAwaitNextValue(
        time: Long = 2,
        timeUnit: TimeUnit = TimeUnit.SECONDS,
        afterObserve: ()-> Unit = {}
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(o: T?) {
            data = o
            latch.countDown()
            // remove current observer of the LiveData that invoked the method getOrAwaitValue()
            this@getOrAwaitNextValue.removeObserver(this)
        }
    }

    // pass the observer to the LiveData object that invokes the method
    this.observeForever(observer)

    try {
        afterObserve.invoke()
        // don't wait indefinitely for the livedata to be set
        if (!latch.await(time, timeUnit)) {
            throw TimeoutException("LiveData value was never set ")
        }
    } finally {
        // in any way remove the observer from the LiveData
        this.removeObserver(observer)
    }

    @Suppress("UNCHECKED_CAST")
    return data as T
}