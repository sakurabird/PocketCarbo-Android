package com.sakurafish.pockettoushituryou.shared.ext

/** Thanks
 * This class uses DroidKaigi source code. https://github.com/DroidKaigi/conference-app-2019
 */

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.shopify.livedataktx.*

inline fun <T> LiveData<T>.changed(
    owner: LifecycleOwner,
    crossinline onChanged: (T) -> Unit
): Removable<T> {
    return nonNull().distinct().observe(owner, onChanged)
}

inline fun <T> LiveData<T?>.changedNonNull(
    owner: LifecycleOwner,
    crossinline onChanged: (T) -> Unit
): Removable<T> {
    @Suppress("UNCHECKED_CAST")
    return (nonNull().distinct() as SupportMediatorLiveData<T>).observe(owner, onChanged)
}

inline fun <T : Any> LiveData<T>.changedForever(
    crossinline onChanged: (T) -> Unit
): Removable<T> {
    return nonNull().distinct().observe(onChanged)
}

fun <T, R> LiveData<T>.mapNotNull(mapper: (T?) -> R?): LiveData<R> = map(mapper).nonNull()

fun <T : Any> LiveData<T>.requireValue(): T = requireNotNull(value)
