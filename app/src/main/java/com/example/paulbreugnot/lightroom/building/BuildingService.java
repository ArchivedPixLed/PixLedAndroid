package com.example.paulbreugnot.lightroom.building;
import com.example.paulbreugnot.lightroom.room.Room;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
public interface BuildingService {

    @GET("buildings")
    Call<List<Building>> listBuildings();

    @GET("buildings/{buildingId}/rooms")
    Call<List<Room>> listBuildingRooms(@Path("buildingId") long id);

}