package com.sakurafish.pockettoushituryou

import com.sakurafish.pockettoushituryou.data.db.entity.Favorite
import com.sakurafish.pockettoushituryou.data.db.entity.Food
import com.sakurafish.pockettoushituryou.data.db.entity.Kind

val kindA = Kind(1, "A", "あ", 1)
val kindB = Kind(2, "B", "い", 2)
val kindC = Kind(3, "C", "う", 2)

val foodA = Food(
        1,
        "A",
        155,
        "1個",
        30.8f,
        47.8f,
        371f,
        11f,
        14.4f,
        9.3f,
        1.1f,
        "たまご",
        "備考1",
        1,
        2,
        kindB)
val foodB = Food(
        2,
        "B",
        155,
        "1個",
        40.8f,
        47.8f,
        371f,
        11f,
        14.4f,
        9.3f,
        1.1f,
        "たまごやき",
        "備考1",
        1,
        1,
        kindA)
val foodC = Food(
        3,
        "C",
        155,
        "1個",
        50.8f,
        47.8f,
        371f,
        11f,
        14.4f,
        9.3f,
        1.1f,
        "ふれんちとーすと",
        "備考1",
        2,
        1,
        kindA)
val foodD = Food(
        4,
        "D",
        154,
        "1個",
        49.8f,
        47.8f,
        371f,
        11f,
        16.4f,
        10.3f,
        1.1f,
        "たまちゃん",
        "備考1",
        2,
        1,
        kindA)

val favoriteA = Favorite(1, foodA, 0)
val favoriteB = Favorite(2, foodB, 100)
val favoriteC = Favorite(3, foodC, 1000)
val favoriteD = Favorite(4, foodD, 10000)