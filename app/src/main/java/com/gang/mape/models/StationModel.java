package com.gang.mape.models;

import android.net.Uri;
import android.os.Parcel;
import android.util.Log;

import androidx.annotation.Nullable;

import com.gang.mape.CustomInfoWindowAdapter;
import com.gang.mape.MapsActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.AddressComponents;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlusCode;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class StationModel {
    private static final String TAG = "StationModel";
    private static final float DEFAULT_ZOOM = 15f;
    private GoogleMap mMap;
    
    private LatLng location;

    private String icon;
    private String id;
    private String place_id;
    private String name;
    private String vicinity;
    private int rating;

    private Place placeHandle = null;

    public Place getPlaceHandle() {
        return placeHandle;
    }

    public void setPlaceHandle(Place placeHandle) {
        this.placeHandle = placeHandle;
    }


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

    public static StationModel fromJSON(JSONObject obj, GoogleMap map){
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
            stationModel.mMap = map;
            return stationModel;
        } catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }

    public String toString(){
        String openHourString;
        try {
            openHourString = placeHandle.getOpeningHours().toString();
        } catch (NullPointerException e){
            openHourString = "No Info";
        }
        assert(placeHandle!=null);
        return "Address: " + getVicinity() + "\n" +
                "Phone: " + placeHandle.getPhoneNumber() + "\n" +
                "Opening Hours: " + openHourString;
    }

    public void moveToMe(){
        Log.d(TAG, "moveCamera: moving the camera to: " + placeHandle.getAddress() );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(placeHandle.getLatLng(), DEFAULT_ZOOM));
        

        if(placeHandle != null){
            try{
                if(!placeHandle.getName().equals("My Location")) {
                    String snippet = "Address: " + placeHandle.getAddress() + "\n" +
                            "Phone Number: " + placeHandle.getPhoneNumber() + "\n" +
                            "Website: " + placeHandle.getWebsiteUri() + "\n" +
                            "Price Rating: " + placeHandle.getRating() + "\n";
                }
            }catch (NullPointerException e){
                Log.e(TAG, "moveCamera: NullPointerException " + e.getMessage() );
            }
        }
    }
}
