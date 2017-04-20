package com.sakurafish.pockettoushituryou.api;

import io.reactivex.Single;
import retrofit2.http.GET;

public interface DummyDataService {

    @GET("/api/v1/dummy_api/status")
    Single<DummyData> getDummyData();
}
