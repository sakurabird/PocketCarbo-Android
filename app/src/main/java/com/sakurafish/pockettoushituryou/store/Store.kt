package com.sakurafish.pockettoushituryou.store

/** Thanks
 * This class uses DroidKaigi source code. https://github.com/DroidKaigi/conference-app-2019
 */

import androidx.lifecycle.ViewModel

open class Store : ViewModel() {
    private val onClearHooks: MutableList<() -> Unit> = mutableListOf()
    fun addHook(hook: () -> Unit) {
        onClearHooks.add(hook)
    }

    override fun onCleared() {
        super.onCleared()
        onClearHooks.forEach { it() }
    }
}
