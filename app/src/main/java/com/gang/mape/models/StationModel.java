package com.gang.mape.models;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

public class StationModel {
    private LatLng location;

    private String icon;
    private String id;
    private String place_id;
    private String name;
    private String vicinity;
    private int rating;

    public LatLng getLocation() {
        return location;
    }

    public String getIcon() {
        return icon;
    }

    public String getId() {
        return id;
    }

    public String getPlace_id() {
        return place_id;
    }

    public String getName() {
        return name;
    }

    public String getVicinity() {
        return vicinity;
    }

    public int getRating(){
        return rating;
    }

    public static StationModel fromJSON(JSONObject obj){
        try {
            StationModel stationModel = new StationModel();
            stationModel.location = new LatLng(
                    obj.getJSONObject("geometry").getJSONObject("location").getDouble("lat"),
                    obj.getJSONObject("geometry").getJSONObject("location").getDouble("lng"));
            stationModel.icon = obj.getString("icon");
            stationModel.id = obj.getString("id");
            stationModel.place_id = obj.getString("place_id");
            stationModel.name = obj.getString("name");
            stationModel.vicinity = obj.getString("vicinity");
            stationModel.rating = obj.getInt("rating");
            return stationModel;
        } catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }

    public String toString(){
        return "Address: " + getVicinity() + "\n" +
                "Rating: " + getRating() + "\n";

    }
}
