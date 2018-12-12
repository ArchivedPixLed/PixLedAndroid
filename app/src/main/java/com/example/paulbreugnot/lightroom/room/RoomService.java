package com.example.paulbreugnot.lightroom.room;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RoomService {
    String ENDPOINT = "http://192.168.12.1:8080/api/";

    @GET("rooms")
    Call<List<Room>> listRooms();

}
