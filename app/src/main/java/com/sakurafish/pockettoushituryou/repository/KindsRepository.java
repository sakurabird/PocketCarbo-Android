package com.sakurafish.pockettoushituryou.repository;

import com.sakurafish.pockettoushituryou.model.Kinds;
import com.sakurafish.pockettoushituryou.model.OrmaDatabase;

import java.util.List;

import javax.inject.Inject;

public class KindsRepository {
    private final static String TAG = KindsRepository.class.getSimpleName();

    private final OrmaDatabase orma;

    @Inject
    KindsRepository(OrmaDatabase orma) {
        this.orma = orma;
    }

    public String findName(int kindId) {
        List<Kinds> kinds = orma.relationOfKinds().selector().idEq(kindId).executeAsObservable().toList().blockingGet();
        if (kinds == null || kinds.size() == 0) return "";
        String name = kinds.get(0).name;
        return name != null ? name : "";
    }
}