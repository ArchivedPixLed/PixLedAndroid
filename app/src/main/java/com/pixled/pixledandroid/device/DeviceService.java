package com.pixled.pixledandroid.device;

import com.pixled.pixledserver.core.color.ColorDto;
import com.pixled.pixledserver.core.device.base.DeviceDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface DeviceService {

    @GET("devices")
    Call<List<DeviceDto>> listDevices();

    @PUT("devices/{id}/switch")
    Call<DeviceDto> switchDevice(@Path("id") int id);

    // @Headers("Content-Type: text;charset=UTF-8")
    @PUT("devices/{id}/color")
    Call<DeviceDto> changeDeviceColor(@Path("id") int id,
                                      @Header("Content-Type") String contentType,
                                      @Body ColorDto newColor);

    @PUT("devices/{id}")
    Call<DeviceDto> updateDevice(@Path("id") int id,
                                 @Header("Content-Type") String contentType,
                                 @Body DeviceDto deviceDto);

    @DELETE("devices/{id}")
    Call<Void> deleteDevice(@Path("id") int id);
}
