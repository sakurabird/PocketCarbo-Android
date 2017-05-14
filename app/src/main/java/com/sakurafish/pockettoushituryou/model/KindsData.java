package com.sakurafish.pockettoushituryou.model;

import java.util.List;

public class KindsData {
    public static final int KINDS_ALL = 0;

    public List<Kinds> getKinds() {
        return kinds;
    }

    public void setKinds(List<Kinds> kinds) {
        this.kinds = kinds;
    }

    private List<Kinds> kinds;
}
