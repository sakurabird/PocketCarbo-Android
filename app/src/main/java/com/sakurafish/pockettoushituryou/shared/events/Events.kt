package com.sakurafish.pockettoushituryou.shared.events

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Events @Inject constructor() {

    // State for showing the tutorial after install
    private val _showcaseState = MutableStateFlow(ShowcaseState.NONE)
    val showcaseState: StateFlow<ShowcaseState> = _showcaseState

    fun setShowcaseState(showcaseState: ShowcaseState) {
        _showcaseState.value = showcaseState
    }

    // State to populate the initial data
    private val _dataPopulateState = MutableStateFlow(PopulateState.NONE)
    val dataPopulateState: StateFlow<PopulateState> = _dataPopulateState

    fun setDataPopulateState(dataPopulateState: PopulateState) {
        _dataPopulateState.value = dataPopulateState
    }

    // Event to update Favorite icon
    private val _favoritesClickedEvent = MutableSharedFlow<HostClass>()
    val favoritesClickedEvent = _favoritesClickedEvent.asSharedFlow()

    suspend fun invokeFavoritesClickedEvent(hostClass: HostClass) =
        _favoritesClickedEvent.emit(hostClass)
}

enum class ShowcaseState {
    NONE, // アプリ起動時の初期状態でチュートリアル表示完了後は常にこの状態となる
    READY, // 初期データ投入が完了した際にREADYとなる
    PROCEEDED // MainActivityで表示すべきチュートリアル画面がすべて表示し終えたときにPROSEEDEDとなる(続きはFoodsFragmentから表示する)
}

enum class PopulateState {
    NONE, // アプリ起動時の初期状態
    POPULATE, // データ投入中
    POPULATED // データ投入完了 一旦初期データ投入後はデータバージョンが上がらない限り常にこの状態となる
}

enum class HostClass {
    FOODS,
    FAVORITES,
    SEARCH
}
