package com.example.paulbreugnot.lightroom.light;

import retrofit2.Call;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface LightService {

    // String ENDPOINT = "http://192.168.12.1:8080/api/";
    String ENDPOINT = "http://10.103.1.211:8080/api/";

    @PUT("lights/{id}/switch")
    Call<Light> switchLight(@Path("id") long idLight);
}
