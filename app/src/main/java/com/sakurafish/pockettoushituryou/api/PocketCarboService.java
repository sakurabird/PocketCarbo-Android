package com.sakurafish.pockettoushituryou.api;

import com.sakurafish.pockettoushituryou.model.FoodsData;

import io.reactivex.Single;
import retrofit2.http.GET;

public interface PocketCarboService {

    @GET("/api/v1/foods")
    Single<FoodsData> getFoodsData();
}
