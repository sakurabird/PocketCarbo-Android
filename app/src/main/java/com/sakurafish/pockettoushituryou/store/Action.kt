package com.sakurafish.pockettoushituryou.store

import com.sakurafish.pockettoushituryou.viewmodel.HostClass

/** Thanks
 * This class uses DroidKaigi source code. https://github.com/DroidKaigi/conference-app-2019
 */

sealed class Action {
    data class FoodsLoadingStateChanged(val loadingState: FoodsStore.PopulateState) : Action()
    data class FavoritesFoodsUpdated(val hostClass: HostClass) : Action()
    object ShowcaseReady : Action()
    object ShowcaseProceeded : Action()
}
