package com.sakurafish.pockettoushituryou.viewmodel;

import com.sakurafish.pockettoushituryou.model.FoodsData;

public class FoodViewModel {

//    public ObservableField<FoodsData.Foods> foods;

    public String name;

    FoodViewModel(FoodsData.Foods foods) {
//        this.foods = new ObservableField<>(foods);
        this.name = foods.name;
    }
}
