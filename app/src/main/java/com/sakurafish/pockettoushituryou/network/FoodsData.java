package com.sakurafish.pockettoushituryou.network;

import java.io.Serializable;
import java.util.ArrayList;

public class FoodsData implements Serializable {
    public ArrayList<Foods> foods;

    public class Foods {
        public int id;
        public String name;
        public int weight;
        public String weight_hint;
        public float carbohydrate_per_100;
        public float carbohydrate_per_weight;
        public float calory;
        public float protein;
        public float fat;
        public float sodium;
        public int type_id;
        public int kind_id;
    }
}
