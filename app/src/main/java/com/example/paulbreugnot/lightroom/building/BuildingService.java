package com.example.paulbreugnot.lightroom.building;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
public interface BuildingService {

    String ENDPOINT = "http://192.168.12.1:8080/api/";

    @GET("buildings")
    Call<List<Building>> listBuildings();

//    @GET("/search/repositories")
//    List<Building> searchRepos(@Query("q") String query);
}