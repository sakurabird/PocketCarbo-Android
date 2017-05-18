package com.sakurafish.pockettoushituryou.model;

import java.util.List;

public class FoodsData {
    public static final int KINDS_ALL = 0;

    private List<Kinds> kinds;
    private List<Foods> foods;

    public List<Kinds> getKinds() {
        return kinds;
    }

    public void setKinds(List<Kinds> kinds) {
        this.kinds = kinds;
    }

    public List<Foods> getFoods() {
        return foods;
    }

    public void setFoods(List<Foods> foods) {
        this.foods = foods;
    }
}
