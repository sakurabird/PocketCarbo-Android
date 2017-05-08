package com.sakurafish.pockettoushituryou.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class KindsData implements Parcelable {
    public static final int KINDS_ALL = 0;
    public ArrayList<Kinds> kinds;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(kinds);
    }

    public static final Parcelable.Creator<KindsData> CREATOR = new Parcelable.Creator<KindsData>() {
        public KindsData createFromParcel(Parcel in) {
            return new KindsData(in);
        }

        public KindsData[] newArray(int size) {
            return new KindsData[size];
        }
    };

    private KindsData(Parcel in) {
        kinds = in.createTypedArrayList(Kinds.CREATOR);
    }

    public KindsData(ArrayList<Kinds> kinds) {
        this.kinds = kinds;
    }

    public static class Kinds implements Parcelable {
        public int id;
        public String name;
        public int type_id;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(id);
            dest.writeString(name);
            dest.writeInt(type_id);
        }

        public static final Creator<Kinds> CREATOR = new Creator<Kinds>() {
            public Kinds createFromParcel(Parcel in) {
                return new Kinds(in);
            }

            public Kinds[] newArray(int size) {
                return new Kinds[size];
            }
        };

        private Kinds(Parcel in) {
            id = in.readInt();
            name = in.readString();
            type_id = in.readInt();
        }
    }
}
