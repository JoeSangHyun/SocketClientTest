package com.udpsocketclient.sample.dto;

public class LocInfoDto {
    private String tagID;
    private String spaceID;
    private Long x;
    private Long y;
    private Long z;
    private String ins_date;

    public LocInfoDto(String tagID, String spaceID, Long x, Long y, Long z, String ins_date) {
        this.tagID = tagID;
        this.spaceID =spaceID;
        this.x = x;
        this.y = y;
        this.z = z;
        this.ins_date = ins_date;
    }

    public LocInfoDto() {

    }

    public String getTagID() {
        return this.tagID;
    }

    public String getSpaceID() {
        return this.spaceID;
    }

    public Long getX() {
        return x;
    }

    public Long getY() {
        return y;
    }

    public Long getZ() {
        return z;
    }

    public String getIns_date() {
        return ins_date;
    }

    public void setTagID(String tagID) {
        this.tagID = tagID;
    }

    public void setSpaceID(String spaceID) {
        this.spaceID = spaceID;
    }

    public void setX(Long x) {
        this.x = x;
    }

    public void setY(Long y) {
        this.y = y;
    }

    public void setZ(Long z) {
        this.z = z;
    }

    public void setIns_date(String ins_date) {
        this.ins_date = ins_date;
    }

    public String toString() {
        return "{" +
                "tagID:" +  tagID + "," +
                "spaceId:" + spaceID + "," +
                "currentPosition: {"+
                        "x:" + x + "," +
                        "y:" + y + "," +
                        "z:" + z +
                    "}" +
                "}";
    }
}
