package com.example.paulbreugnot.lightroom.light;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface LightService {

    @PUT("lights/{id}/switch")
    Call<Light> switchLight(@Path("id") long idLight);

    // @Headers("Content-Type: text;charset=UTF-8")
    @PUT("lights/{id}/color")
    Call<Light> changeLightColor(@Path("id") long idLight,
                                 @Header("Content-Type") String contentType,
                                 @Body JsonColor newColor);
}
