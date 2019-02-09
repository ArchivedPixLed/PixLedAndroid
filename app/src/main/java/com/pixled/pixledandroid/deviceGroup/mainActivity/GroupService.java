package com.pixled.pixledandroid.deviceGroup.mainActivity;

import com.pixled.pixledserver.core.device.base.DeviceDto;
import com.pixled.pixledserver.core.group.DeviceGroupDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface GroupService {

    @GET("groups")
    Call<List<DeviceGroupDto>> listGroups();

    @GET("groups/{id}")
    Call<DeviceGroupDto> getGroup(@Path("id") int id);

    @GET("groups/{id}/devices")
    Call<List<DeviceDto>> listGroupDevices(@Path("id") int id);

    @PUT("groups/{id}/switch")
    Call<List<DeviceDto>> switchGroup(@Path("id") int id);

    @PUT("groups/{id}")
    Call<DeviceGroupDto> updateGroup(@Path("id") int id, @Body DeviceGroupDto deviceGroupDto);

}
