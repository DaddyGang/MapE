package com.gang.mape;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gang.mape.models.CarStatus;
import com.gang.mape.models.PlaceInfo;
import com.gang.mape.models.StationModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import eo.view.batterymeter.BatteryMeterView;

public class MapsActivity extends FragmentActivity implements CarSelectBottomSheetDialog.BottomSheetListener, OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {


    private PlaceInfo mPlace;
    private Marker mMarker;

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private static final String TAG = "MapsActivity";

    private GoogleMap mMap;
    private Boolean mLocationPermissionsGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private PlacesClient mPlacesClient;
    private ArrayList<StationModel> stationsNearby;

    //widgets
    private AutoCompleteTextView mSearchText;
    private ImageView mGps, mInfo;
    private LinearLayout mCarSettingButton;
    private ImageView mCarImage;
    private BatteryMeterView mBatteryMeterView;
    private BottomSheetBehavior mBottomSheetBehavior;
    private RecyclerView mStationListView;
    private StationRecyclerViewAdapter mStationAdapter;
    private LinearLayoutManager mLayoutManager;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));
    private static final String NEARBY_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";

    private List<Place.Field> placeFields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mSearchText = (AutoCompleteTextView) findViewById(R.id.input_search);
        mGps = (ImageView) findViewById(R.id.icon_gps);
        mInfo = (ImageView) findViewById(R.id.icon_info);
        mCarSettingButton = (LinearLayout) findViewById(R.id.button_setting_bottom_sheet);
        mCarImage = findViewById(R.id.icon_car);
        mBatteryMeterView = findViewById(R.id.battery_meter);

        mCarSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CarSelectBottomSheetDialog bottomSheet = new CarSelectBottomSheetDialog();
                bottomSheet.show(getSupportFragmentManager(), "CarSelectionBottomSheet");
            }
        });

        getLocationPermission();

        placeFields = Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG,
                Place.Field.WEBSITE_URI,
                Place.Field.ADDRESS,
                Place.Field.VIEWPORT,
                Place.Field.RATING,
                Place.Field.PHONE_NUMBER,
                Place.Field.PHOTO_METADATAS,
                Place.Field.OPENING_HOURS,
                Place.Field.PRICE_LEVEL,
                Place.Field.USER_RATINGS_TOTAL
        );

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        //SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        //        .findFragmentById(R.id.map);
       // mapFragment.getMapAsync(this);
        View bottomSheet = findViewById(R.id.bottom_sheet_station_view);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        FloatingActionButton buttonExpand = findViewById(R.id.btn_expand);
        buttonExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        stationsNearby = new ArrayList<>();

        // TODO: Debug button
        Button debug = findViewById(R.id.debug_btn);
        debug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: There are "+ mStationAdapter.getItemCount());
                for (StationModel station: stationsNearby){
                    Log.d(TAG, "onClick: " + station.getPlaceHandle().getPhotoMetadatas());
                }
                mStationAdapter.notifyDataSetChanged();
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            init();
            initStationRecyclerView();
            hideSoftKeyboard();
        }
    }

    private void init(){
        Log.d(TAG, "init: initializing");
        if (!Places.isInitialized()) {
            Log.d(TAG, "init: api key is: " + getString(R.string.google_maps_key));
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        }
        AutocompleteSessionToken autocompleteSessionToken = AutocompleteSessionToken.newInstance();
        mPlacesClient = Places.createClient(getApplicationContext());

        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this, mPlacesClient, autocompleteSessionToken);

        mSearchText.setAdapter(mPlaceAutocompleteAdapter);
        mSearchText.setOnItemClickListener(mAutocompleteClickListener);

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                Log.d(TAG, "onEditorAction: " + actionId);
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){

                    //execute our method for searching
                    geoLocate();
                }

                return false;
            }
        });
        
        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked gps icon");
                getDeviceLocation();
            }
        });
        mInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked place info");
                try{
                    if(mMarker.isInfoWindowShown()){
                        mMarker.hideInfoWindow();
                    } else {
                        Log.d(TAG, "onClick: place info: " + mPlace.toString());
                        mMarker.showInfoWindow();
                    }
                } catch(NullPointerException e){
                    Log.e(TAG, "onClick: NullPointerException" + e.getMessage() );
                }
            }
        });

    }

    private void geoLocate(){
        Log.d(TAG, "geoLocate: geolocating");

        String searchString = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(MapsActivity.this);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString, 1);
        }catch (IOException e){
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage() );
        }

        if(list.size() > 0){
            Address address = list.get(0);

            Log.d(TAG, "geoLocate: found a location: " + address.toString());

            mPlace = new PlaceInfo();
            mPlace.setName(address.getAddressLine(0));

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, mPlace);

        }
    }

    private void initStationRecyclerView(){
        // TODO: init recycle
        mStationListView = findViewById(R.id.station_recycle_layout);
        mLayoutManager = new LinearLayoutManager(this);
        mStationAdapter = new StationRecyclerViewAdapter(mBottomSheetBehavior, this, stationsNearby);
        mStationListView.setAdapter(mStationAdapter);
        mStationListView.setLayoutManager(mLayoutManager);
        Log.d(TAG, "initStationRecyclerView: Initilized");
    }

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionsGranted){

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();

                            getStationsNearBy(currentLocation);
                            mStationAdapter.notifyDataSetChanged();


                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM, null);

                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MapsActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }

    private void getStationsNearBy(Location location){
        // TODO: get stations near by
        Log.d(TAG, "getStationsNearBy: ");
        RequestParams params = new RequestParams();
        params.put("location", location.getLatitude() + "," + location.getLongitude());
        params.put("radius", "3000");
        // params.put("type", "gas_station");
        params.put("keyword", "charge");
        params.put("key", getResources().getString(R.string.google_maps_key));
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(NEARBY_SEARCH_URL, params, new JsonHttpResponseHandler(){

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(MapsActivity.this, "Failed!" + throwable.toString(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: Status code" + statusCode);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try{
                    JSONArray c = response.getJSONArray("results");
                    for(int i = 0; i < c.length(); i++){
                        StationModel thatStation = StationModel.fromJSON(c.getJSONObject(i));
                        String snippet = thatStation.toString();
                        MarkerOptions options = new MarkerOptions()
                                .position(thatStation.getLocation())
                                .title(thatStation.getName())
                                .snippet(snippet);
                        mMap.addMarker(options);
                        patchPlaceInfoForStations(thatStation);
                        stationsNearby.add(thatStation);
                    }
                    Log.d(TAG, "Success! JSON count: " + stationsNearby.size());
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.d(TAG, "onFailure: 2" + statusCode);
            }


        });
    }

    private void patchPlaceInfoForStations(final StationModel someStation){
        //TODO: patch place info
        final String placeId = someStation.getPlace_id();

        // Construct a request object, passing the place ID and fields array.
        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields)
                .build();

        // Add a listener to handle the response.
        mPlacesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
            @Override
            public void onSuccess(FetchPlaceResponse response) {
                Place place = response.getPlace();
                someStation.setPlaceHandle(place);
                Log.d(TAG, "Place set" + someStation.getIcon());
                mStationAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            if (e instanceof ApiException) {
                ApiException apiException = (ApiException) e;
                int statusCode = apiException.getStatusCode();
                // Handle error with given status code.
                Log.e(TAG, "Place fetch failed " + e.getMessage());
                Toast.makeText(getApplicationContext(), "Place fetch failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            }
        });
    }

    private void moveCamera(LatLng latLng, float zoom, PlaceInfo aPlace){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        mMap.clear();
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(MapsActivity.this));

        if(aPlace != null){
            try{
                if(!aPlace.getName().equals("My Location")) {
                    String snippet = "Address: " + aPlace.getAddress() + "\n" +
                            "Phone Number: " + aPlace.getPhoneNumber() + "\n" +
                            "Website: " + aPlace.getWebsiteUri() + "\n" +
                            "Price Rating: " + aPlace.getRating() + "\n";

                    MarkerOptions options = new MarkerOptions()
                            .position(latLng)
                            .title(aPlace.getName())
                            .snippet(snippet);
                    if(mMarker!=null) mMarker.remove();
                    mMarker = mMap.addMarker(options);
                }
            }catch (NullPointerException e){
                Log.e(TAG, "moveCamera: NullPointerException " + e.getMessage() );
            }
        }

        hideSoftKeyboard();
    }

    private void initMap(){
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        //SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        //        .findFragmentById(R.id.map);
        // mapFragment.getMapAsync(this);

    }

    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }

    private void hideSoftKeyboard(){
        Log.i(TAG, "hideSoftKeyboard: hide");
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            hideSoftKeyboard();

            final AutocompletePrediction item = mPlaceAutocompleteAdapter.getItem(i);
            final String placeId = item.getPlaceId();

            // Construct a request object, passing the place ID and fields array.
            FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields)
                    .build();

            // Add a listener to handle the response.
            mPlacesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                @Override
                public void onSuccess(FetchPlaceResponse response) {
                    Place place = response.getPlace();
                    grabPlace(place);
                    Log.i(TAG, "Place found: " + place);
                    moveCamera(new LatLng(mPlace.getLatLng().latitude, mPlace.getLatLng().longitude), DEFAULT_ZOOM, mPlace);
                    Log.i(TAG, "Place stored: " + mPlace.toString());

                    hideSoftKeyboard();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (e instanceof ApiException) {
                        ApiException apiException = (ApiException) e;
                        int statusCode = apiException.getStatusCode();
                        // Handle error with given status code.
                        Log.e(TAG, "Place not found: " + e.getMessage());
                        Toast.makeText(getApplicationContext(), "Place not found" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    };

    private void grabPlace(Place place){
        try {
            mPlace = new PlaceInfo(place);
        } catch (NullPointerException e){
            Log.e(TAG, "grabPlace: "+e.getMessage());
        }
    }

    @Override
    public void onCarSelected(CarStatus instance) {
        mCarImage.setImageResource(instance.getModelImage());
        mBatteryMeterView.setChargeLevel(instance.getBattery());
    }
}
