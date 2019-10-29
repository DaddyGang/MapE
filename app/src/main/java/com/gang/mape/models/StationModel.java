package com.gang.mape.models;

import android.net.Uri;
import android.os.Parcel;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.libraries.places.api.model.AddressComponents;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlusCode;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class StationModel {
    private LatLng location;

    private String icon;
    private String id;
    private String place_id;
    private String name;
    private String vicinity;
    private int rating;


    public Place getPlaceHandle() {
        return placeHandle;
    }

    public void setPlaceHandle(Place placeHandle) {
        this.placeHandle = placeHandle;
    }

    private Place placeHandle = new Place() {
        @Nullable
        @Override
        public String getAddress() {
            return null;
        }

        @Nullable
        @Override
        public AddressComponents getAddressComponents() {
            return null;
        }

        @Nullable
        @Override
        public List<String> getAttributions() {
            return null;
        }

        @Nullable
        @Override
        public String getId() {
            return null;
        }

        @Nullable
        @Override
        public LatLng getLatLng() {
            return null;
        }

        @Nullable
        @Override
        public String getName() {
            return null;
        }

        @Nullable
        @Override
        public OpeningHours getOpeningHours() {
            return null;
        }

        @Nullable
        @Override
        public String getPhoneNumber() {
            return null;
        }

        @Nullable
        @Override
        public List<PhotoMetadata> getPhotoMetadatas() {
            return null;
        }

        @Nullable
        @Override
        public PlusCode getPlusCode() {
            return null;
        }

        @Nullable
        @Override
        public Integer getPriceLevel() {
            return null;
        }

        @Nullable
        @Override
        public Double getRating() {
            return null;
        }

        @Nullable
        @Override
        public List<Type> getTypes() {
            return null;
        }

        @Nullable
        @Override
        public Integer getUserRatingsTotal() {
            return null;
        }

        @Nullable
        @Override
        public Integer getUtcOffsetMinutes() {
            return null;
        }

        @Nullable
        @Override
        public LatLngBounds getViewport() {
            return null;
        }

        @Nullable
        @Override
        public Uri getWebsiteUri() {
            return null;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {

        }
    };


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
