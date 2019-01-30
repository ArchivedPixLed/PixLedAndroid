package com.pixled.pixledandroid.device;

import com.pixled.pixledserver.core.color.ColorDto;
import com.pixled.pixledserver.core.device.base.DeviceDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface DeviceService {

    @PUT("devices/{id}/switch")
    Call<DeviceDto> switchDevice(@Path("id") long idLight);

    // @Headers("Content-Type: text;charset=UTF-8")
    @PUT("devices/{id}/color")
    Call<DeviceDto> changeDeviceColor(@Path("id") long idLight,
                                      @Header("Content-Type") String contentType,
                                      @Body ColorDto newColor);
}
