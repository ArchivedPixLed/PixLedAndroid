package com.pixled.pixledandroid.utils;

import java.util.Objects;

public class DeviceGroupIdPair implements Comparable {

    private Integer deviceId;
    private Integer groupId;

    public DeviceGroupIdPair(Integer deviceId, Integer groupId) {
        this.deviceId = deviceId;
        this.groupId = groupId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceGroupIdPair that = (DeviceGroupIdPair) o;
        return deviceId.equals(that.getDeviceId()) &&
                groupId.equals(that.getGroupId());
    }

    @Override
    public int hashCode() {
        return deviceId * 31 + groupId;
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public Integer getGroupId() {
        return groupId;
    }

    @Override
    public int compareTo(Object o) {
        if (this.equals(o)) {
            return 0;
        }
        return -1;
    }
}
