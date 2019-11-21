package com.sakurafish.pockettoushituryou.store

import androidx.lifecycle.LiveData
import com.sakurafish.pockettoushituryou.shared.ext.toLiveDataEndless
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FoodsStore @Inject constructor(
        dispatcher: Dispatcher
) : Store() {
    sealed class PopulateState {
        object Prepare : PopulateState()

        // Populating from Json file
        object Populate : PopulateState()

        // Populated completed
        object Populated : PopulateState()
    }

    @ExperimentalCoroutinesApi
    val populateState: LiveData<PopulateState> = dispatcher.subscribe<Action.FoodsLoadingStateChanged>()
            .map { it.loadingState }
            .toLiveDataEndless(PopulateState.Prepare)

    @ExperimentalCoroutinesApi
    val favoritesChanged: LiveData<Action.FavoritesFoodsUpdated> = dispatcher.subscribe<Action.FavoritesFoodsUpdated>()
            .map { it }
            .toLiveDataEndless(null)

    @ExperimentalCoroutinesApi
    val showcaseProceeded: LiveData<Action.ShowcaseProceeded> = dispatcher.subscribe<Action.ShowcaseProceeded>()
            .map { it }
            .toLiveDataEndless(null)
}
