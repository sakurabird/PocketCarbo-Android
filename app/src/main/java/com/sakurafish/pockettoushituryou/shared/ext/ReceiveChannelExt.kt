package com.sakurafish.pockettoushituryou.shared.ext

/** Thanks
 * This class uses DroidKaigi source code. https://github.com/DroidKaigi/conference-app-2019
 */

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import com.sakurafish.pockettoushituryou.store.Store
import com.shopify.livedataktx.SingleLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch

@MainThread fun <T> ReceiveChannel<T>.toSingleLiveData(
        store: Store,
        defaultValue: T? = null
): SingleLiveData<T> {
    return object : SingleLiveData<T>(), CoroutineScope by GlobalScope {
        init {
            if (defaultValue != null) {
                value = defaultValue
            }
            val job = launch(CoroutinePlugin.mainDispatcher) {
                // observe forever
                for (element in this@toSingleLiveData) {
                    postValue(element)
                }
            }
            store.addHook { job.cancel() }
        }
    }
}

@MainThread fun <T> ReceiveChannel<T>.toSingleLiveDataEndless(
        defaultValue: T? = null
): SingleLiveData<T> {
    return object : SingleLiveData<T>(), CoroutineScope by GlobalScope {
        init {
            if (defaultValue != null) {
                value = defaultValue
            }
            val job = launch(CoroutinePlugin.mainDispatcher) {
                // observe forever
                for (element in this@toSingleLiveDataEndless) {
                    postValue(element)
                }
            }
        }
    }
}

@MainThread fun <T> ReceiveChannel<T>.toLiveData(
        store: Store,
        defaultValue: T? = null
): LiveData<T> {
    return object : LiveData<T>(), CoroutineScope by GlobalScope {
        init {
            if (defaultValue != null) {
                value = defaultValue
            }
            val job = launch(CoroutinePlugin.mainDispatcher) {
                for (element in this@toLiveData) {
                    postValue(element)
                }
            }
            store.addHook { job.cancel() }
        }
    }
}

@MainThread fun <T> ReceiveChannel<T>.toLiveDataEndless(
        defaultValue: T? = null
): LiveData<T> {
    return object : LiveData<T>(), CoroutineScope by GlobalScope {
        init {
            if (defaultValue != null) {
                value = defaultValue
            }
            launch(CoroutinePlugin.mainDispatcher) {
                // observe forever
                for (element in this@toLiveDataEndless) {
                    postValue(element)
                }
            }
        }
    }
}
