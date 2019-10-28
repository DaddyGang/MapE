package com.gang.mape.models;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;

import java.util.List;

/**
 * Created by User on 10/2/2017.
 */

public class PlaceInfo {

    private String name;
    private String address;
    private String phoneNumber;
    private String id;
    private Uri websiteUri;
    private LatLng latlng;

    private LatLngBounds viewPort;
    private List<PhotoMetadata> photo;
    private OpeningHours openingHours;

    private double rating;
    private int priceLevel;
    private int userRatingTotal;

    public PlaceInfo(Place thatPlace){
        name = thatPlace.getName()==null? null : thatPlace.getName();
        address = thatPlace.getAddress()==null? null : thatPlace.getAddress();
        phoneNumber = thatPlace.getPhoneNumber()==null? null : thatPlace.getPhoneNumber();
        id= thatPlace.getId()==null? null : thatPlace.getId();
        websiteUri = thatPlace.getWebsiteUri()==null? null : thatPlace.getWebsiteUri();
        latlng = thatPlace.getLatLng()==null? null : thatPlace.getLatLng();
        viewPort = thatPlace.getViewport()==null? null : thatPlace.getViewport();
        photo = thatPlace.getPhotoMetadatas()==null? null : thatPlace.getPhotoMetadatas();
        rating = thatPlace.getRating()==null? 0 : thatPlace.getRating();
        openingHours = thatPlace.getOpeningHours()==null? null : thatPlace.getOpeningHours();
        priceLevel = thatPlace.getPriceLevel()==null? 0 : thatPlace.getPriceLevel();
        userRatingTotal = thatPlace.getUserRatingsTotal()==null? 0 : thatPlace.getUserRatingsTotal();
    }


    public PlaceInfo() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Uri getWebsiteUri() {
        return websiteUri;
    }

    public void setWebsiteUri(Uri websiteUri) {
        this.websiteUri = websiteUri;
    }

    public LatLng getLatLng() {
        return latlng;
    }

    public void setLatlng(LatLng latlng) {
        this.latlng = latlng;
    }

    public void setRating(double rating) { this.rating = rating; }

    public double getRating(){ return rating; }

    public OpeningHours getOpeningHours() { return openingHours; }

    public void setOpeningHours(OpeningHours openingHours) { this.openingHours = openingHours; }

    public int getPriceLevel() { return priceLevel; }

    public void setPriceLevel(int priceLevel) { this.priceLevel = priceLevel; }

    public int getUserRatingTotal() { return userRatingTotal; }

    public void setUserRatingTotal(int userRatingTotal) { this.userRatingTotal = userRatingTotal; }

    @Override
    public String toString() {
        return "PlaceInfo{" +
                "name='" + name!=null ? name:"NULL" + '\'' +
                ", id='" + id!=null ? id:"NULL" + '\'' +
                ", viewport='" + viewPort!=null? viewPort.toString():"NULL" + '\'' +
                ", address='" + address!=null? address:"NULL" + '\'' +
                ", phoneNumber='" + phoneNumber!=null? phoneNumber:"NULL" + '\'' +
                ", websiteUri=" + websiteUri!=null? websiteUri.toString():"NULL" +
                ", latlng=" + latlng!=null? latlng.toString():"NULL" +
                ", photo='" + photo!=null? photo.toString():"NULL" + '\'' +
                ", opening hours='" + openingHours!=null? openingHours.toString():"NULL" + '\'' +
                ", price level='" + priceLevel + '\'' +
                ", rating=" + rating +
                ", user rating total='" + userRatingTotal + '\'' +
                '}';


    }

    public List<PhotoMetadata> getPhoto() {
        return photo;
    }

    public void setPhoto(List<PhotoMetadata> photo) {
        this.photo = photo;
    }

    public LatLngBounds getViewPort() {
        return viewPort;
    }

    public void setViewPort(LatLngBounds viewPort) {
        this.viewPort = viewPort;
    }
}