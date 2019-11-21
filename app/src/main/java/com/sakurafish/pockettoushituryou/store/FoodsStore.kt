package com.sakurafish.pockettoushituryou.store

import androidx.lifecycle.LiveData
import com.sakurafish.pockettoushituryou.shared.ext.toLiveDataEndless
import kotlinx.coroutines.channels.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FoodsStore @Inject constructor(
        dispatcher: Dispatcher
) {
    sealed class PopulateState {
        object Prepare : PopulateState()

        // Populating from Json file
        object Populate : PopulateState()

        // Populated completed
        object Populated : PopulateState()
    }

    val populateState: LiveData<PopulateState> = dispatcher.subscribe<Action.FoodsLoadingStateChanged>()
            .map { it.loadingState }
            .toLiveDataEndless(PopulateState.Prepare)
}
