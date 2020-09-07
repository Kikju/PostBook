package com.senacor.postbook.util

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import timber.log.Timber

/**
 * Used as a wrapper for data that is exposed via a LiveData that represents an event.
 */
open class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * Returns the content and prevents its use again.
     */
    fun getEventIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = content
}


/**
 * An [Observer] for [Event]s, simplifying the pattern of checking if the [Event]'s content has
 * already been handled.
 *
 * [onEventUnhandledContent] is *only* called if the [Event]'s contents has not been handled.
 */
class EventObserver<T>(private val onEventUnhandledContent: (T) -> Unit): Observer<Event<T>> {
    override fun onChanged(event: Event<T>?) {
        event?.getEventIfNotHandled()?.let(onEventUnhandledContent)
    }
}

class CountingBooleanMutableLiveData: MutableLiveData<Boolean>(false) {
    private var counter = 0

    fun inc() {
        counter++
        if (counter > 0 && value == false)
            value = true
    }

    fun dect() {
        if (counter > 0)
            counter--
        if (counter == 0 && value == true)
            value = false
    }
}


sealed class Resource<T>(
    open val data: T? = null,
    open val throwable: Throwable? = null
) {
    data class Success<T>(override val data: T): Resource<T>(data)
    data class Loading<T>(override val data: T? = null): Resource<T>(data)
    data class Error<T>(override val throwable: Throwable, override val data: T? = null): Resource<T>(data, throwable)
}

fun <T> Resource<T>.processResource(loading: CountingBooleanMutableLiveData, onSuccess: (data: T) -> Unit, onError: (throwable: Throwable) -> Unit) {
    when (this) {
        is Resource.Success -> {
            loading.dect()
            onSuccess(data)
        }
        is Resource.Loading -> loading.inc()
        is Resource.Error -> {
            loading.dect()
            Timber.e(throwable)
            onError(throwable)
        }
    }
}