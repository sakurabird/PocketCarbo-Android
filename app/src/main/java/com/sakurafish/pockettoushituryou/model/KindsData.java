package com.sakurafish.pockettoushituryou.model;

import java.io.Serializable;
import java.util.ArrayList;

public class KindsData implements Serializable {
    public ArrayList<Kinds> kinds;

    public class Kinds {
        public int id;
        public String name;
        public int type_id;
    }
}
