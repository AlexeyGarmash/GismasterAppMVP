package com.example.alex.gismasterappmvp.mvp.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.alex.gismasterappmvp.mvp.models.CityInfoRealm;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Класс-модель информации о месте, в котором необходимо получить погоду.
 */
public class CityInfo implements Parcelable {

    @PrimaryKey
    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("lat")
    @Expose
    private double lat;

    @SerializedName("lon")
    @Expose
    private double lon;

    @SerializedName("status")
    @Expose
    private String status;


    @SerializedName("city")
    @Expose
    private String cityName;


    @SerializedName("country")
    @Expose
    private String countryName;


    @SerializedName("address")
    @Expose
    private String address;

    public final static Creator<CityInfo> CREATOR = new Creator<CityInfo>() {


        @SuppressWarnings({
                "unchecked"
        })
        public CityInfo createFromParcel(Parcel in) {
            return new CityInfo(in);
        }

        public CityInfo[] newArray(int size) {
            return (new CityInfo[size]);
        }

    };


    public CityInfo() {

    }

    protected CityInfo(Parcel in) {
        this.lat = ((double) in.readValue((double.class.getClassLoader())));
        this.lon = ((double) in.readValue((double.class.getClassLoader())));
        this.cityName = ((String) in.readValue((String.class.getClassLoader())));
        this.countryName = ((String) in.readValue((String.class.getClassLoader())));
        this.status = ((String) in.readValue((String.class.getClassLoader())));
        this.address = ((String) in.readValue((String.class.getClassLoader())));
        this.id = ((String) in.readValue((String.class.getClassLoader())));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CityInfo(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    public CityInfoRealm getCityInfoRealm() {
        return new CityInfoRealm(id, lat, lon, status, cityName, countryName, address);
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(lat);
        dest.writeValue(lon);
        dest.writeValue(cityName);
        dest.writeValue(countryName);
        dest.writeValue(status);
        dest.writeValue(address);
        dest.writeValue(id);
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CityInfo) {
            CityInfo toCompare = (CityInfo) obj;
            return this.cityName.equals(toCompare.getCityName())
                    && this.countryName.equals(toCompare.getCountryName())
                    && this.lat == toCompare.getLat()
                    && this.lon == toCompare.getLon();
        }
        return false;
    }
}
