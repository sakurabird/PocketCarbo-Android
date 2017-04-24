package com.sakurafish.pockettoushituryou.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class FoodsData implements Parcelable {
    public ArrayList<Foods> foods;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(foods);
    }

    public static final Parcelable.Creator<FoodsData> CREATOR = new Parcelable.Creator<FoodsData>() {
        public FoodsData createFromParcel(Parcel in) {
            return new FoodsData(in);
        }

        public FoodsData[] newArray(int size) {
            return new FoodsData[size];
        }
    };

    private FoodsData(Parcel in) {
        foods = in.createTypedArrayList(Foods.CREATOR);
    }

    public FoodsData(ArrayList<Foods> foods) {
        this.foods = foods;
    }

    public static class Foods implements Parcelable {
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

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(id);
            dest.writeString(name);
            dest.writeInt(weight);
            dest.writeString(weight_hint);
            dest.writeFloat(carbohydrate_per_100);
            dest.writeFloat(carbohydrate_per_weight);
            dest.writeFloat(calory);
            dest.writeFloat(protein);
            dest.writeFloat(fat);
            dest.writeFloat(sodium);
            dest.writeInt(type_id);
            dest.writeInt(kind_id);
        }

        public static final Creator<Foods> CREATOR = new Creator<Foods>() {
            public Foods createFromParcel(Parcel in) {
                return new Foods(in);
            }

            public Foods[] newArray(int size) {
                return new Foods[size];
            }
        };

        private Foods(Parcel in) {
            id = in.readInt();
            name = in.readString();
            weight = in.readInt();
            weight_hint = in.readString();
            carbohydrate_per_100 = in.readFloat();
            carbohydrate_per_weight = in.readFloat();
            calory = in.readFloat();
            protein = in.readFloat();
            fat = in.readFloat();
            sodium = in.readFloat();
            type_id = in.readInt();
            kind_id = in.readInt();
        }
    }
}
