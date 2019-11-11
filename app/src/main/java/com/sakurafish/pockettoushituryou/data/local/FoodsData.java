package com.sakurafish.pockettoushituryou.data.local;

import com.google.gson.annotations.SerializedName;
import com.sakurafish.pockettoushituryou.data.db.entity.Foods;
import com.sakurafish.pockettoushituryou.data.db.entity.Kinds;

import java.util.List;

public class FoodsData {
    public static final int KINDS_ALL = 0;

    @SerializedName("data_version")
    private int dataVersion;

    private List<Kinds> kinds;

    private List<Foods> foods;

    public int getDataVersion() {
        return dataVersion;
    }

    public void setDataVersion(int dataVersion) {
        this.dataVersion = dataVersion;
    }

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
