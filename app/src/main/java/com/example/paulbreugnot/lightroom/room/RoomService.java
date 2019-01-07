package com.example.paulbreugnot.lightroom.room;

import com.example.paulbreugnot.lightroom.light.Light;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface RoomService {

    @GET("rooms")
    Call<List<Room>> listRooms();

    @GET("rooms/{roomId}")
    Call<Room> getRoom(@Path("roomId") long roomId);

    @GET("rooms/{roomId}/lights")
    Call<List<Light>> listRoomLights(@Path("roomId") long roomId);

    @PUT("rooms/{roomId}/switch")
    Call<List<Light>> switchRoom(@Path("roomId") long roomId);

}
