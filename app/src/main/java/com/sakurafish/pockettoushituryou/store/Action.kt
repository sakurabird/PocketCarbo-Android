package com.sakurafish.pockettoushituryou.store

/** Thanks
 * This class uses DroidKaigi source code. https://github.com/DroidKaigi/conference-app-2019
 */

sealed class Action {
    data class FoodsLoadingStateChanged(val loadingState: FoodsStore.PopulateState) : Action()
}
