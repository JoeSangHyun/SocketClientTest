package com.udpsocketclient.sample.dto;

import lombok.Data;

@Data
public class LocationDto {
    private int id;
    private String name;
    private double x;
    private double y;
    private double z;
    private long spaceId;
    // UWB TAG 알람
    private String deviceId;
    private long count;
    private long timestamp;
}
