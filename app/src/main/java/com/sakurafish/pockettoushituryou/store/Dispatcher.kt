package com.sakurafish.pockettoushituryou.store

/** Thanks
 * This class uses DroidKaigi source code. https://github.com/DroidKaigi/conference-app-2019
 */

import com.sakurafish.pockettoushituryou.shared.ext.CoroutinePlugin
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Singleton
class Dispatcher @Inject constructor() {
    @ExperimentalCoroutinesApi
    private val _actions = BroadcastChannel<Action>(Channel.CONFLATED)
    @ExperimentalCoroutinesApi
    val events: ReceiveChannel<Action> get() = _actions.openSubscription()

    @ExperimentalCoroutinesApi
    inline fun <reified T : Action> subscribe(): ReceiveChannel<T> {
        return events.filterAndCast()
    }

    @ExperimentalCoroutinesApi
    suspend fun dispatch(action: Action) {
        // Make sure calling `_actions.send()` from single thread. We can lose action if
        // `_actions.send()` is called simultaneously from multiple threads
        // https://github.com/Kotlin/kotlinx.coroutines/blob/1.0.1/common/kotlinx-coroutines-core-common/src/channels/ConflatedBroadcastChannel.kt#L227-L230
        withContext(CoroutinePlugin.mainDispatcher) {
            _actions.send(action)
        }
    }

    @ExperimentalCoroutinesApi
    fun launchAndDispatch(action: Action) {
        GlobalScope.launch(CoroutinePlugin.mainDispatcher) {
            _actions.send(action)
        }
    }

    @ExperimentalCoroutinesApi
    inline fun <reified E, reified R : E> ReceiveChannel<E>.filterAndCast(
            context: CoroutineContext = Dispatchers.Unconfined
    ): ReceiveChannel<R> =
            GlobalScope.produce(context, capacity = Channel.UNLIMITED) {
                consumeEach { e ->
                    (e as? R)?.let { send(it) }
                }
            }
}
