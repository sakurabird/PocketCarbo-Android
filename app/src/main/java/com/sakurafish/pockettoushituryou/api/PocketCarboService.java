package com.sakurafish.pockettoushituryou.api;

import com.sakurafish.pockettoushituryou.model.FoodsData;
import com.sakurafish.pockettoushituryou.model.KindsData;

import io.reactivex.Single;
import retrofit2.http.GET;

public interface PocketCarboService {

    @GET("/api/v1/kinds")
    Single<KindsData> getKindsData();

    @GET("/api/v1/foods")
    Single<FoodsData> getFoodsData();
}
