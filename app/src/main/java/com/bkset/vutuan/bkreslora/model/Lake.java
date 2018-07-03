package com.bkset.vutuan.bkreslora.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Phung Dinh Phuc on 29/07/2017.
 */

public class Lake implements Parcelable {
    public int Id;
    public String HoDanName;
    public String Name;
    public String MapUrl;
    public String CreatedDate;

    protected Lake(Parcel in) {
        Id = in.readInt();
        HoDanName = in.readString();
        Name = in.readString();
        MapUrl = in.readString();
        CreatedDate = in.readString();
    }

    public static final Creator<Lake> CREATOR = new Creator<Lake>() {
        @Override
        public Lake createFromParcel(Parcel in) {
            return new Lake(in);
        }

        @Override
        public Lake[] newArray(int size) {
            return new Lake[size];
        }
    };

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getHoDanName() {
        return HoDanName;
    }

    public void setHoDanName(String hoDanName) {
        HoDanName = hoDanName;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getMapUrl() {
        return MapUrl;
    }

    public void setMapUrl(String mapUrl) {
        MapUrl = mapUrl;
    }

    public String getCreatedTime() {
        return CreatedDate;
    }

    public void setCreatedDate(String createdTime) {
        CreatedDate = createdTime;
    }

    public Lake(int id, String hoDanName, String name, String mapUrl, String createdTime) {

        Id = id;
        HoDanName = hoDanName;
        Name = name;
        MapUrl = mapUrl;
        CreatedDate = createdTime;
    }

    @Override
    public String toString() {
        return "Lake{" +
                "Id=" + Id +
                ", HoDanName='" + HoDanName + '\'' +
                ", Name='" + Name + '\'' +
                ", MapUrl='" + MapUrl + '\'' +
                ", CreatedDate='" + CreatedDate + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(Id);
        parcel.writeString(HoDanName);
        parcel.writeString(Name);
        parcel.writeString(MapUrl);
        parcel.writeString(CreatedDate);
    }
}
