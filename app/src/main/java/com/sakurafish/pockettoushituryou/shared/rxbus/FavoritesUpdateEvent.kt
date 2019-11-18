package com.sakurafish.pockettoushituryou.shared.rxbus

import com.sakurafish.pockettoushituryou.viewmodel.HostClass

class FavoritesUpdateEvent(
        val hostClass: HostClass,
        val foodsId: Int
)