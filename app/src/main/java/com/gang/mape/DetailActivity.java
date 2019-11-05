package com.gang.mape;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.gang.mape.models.PlaceInfo;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPhotoResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class DetailActivity extends FragmentActivity implements OnStreetViewPanoramaReadyCallback {
    private static final String TAG = "DetailActivity";
    private Place mPlace;
    private PlacesClient mPlaceClient;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mPlace = (Place) getIntent().getParcelableExtra("StationDetail");
        this.mPlaceClient = Places.createClient(this);

        ImageView mBackBtn = findViewById(R.id.detail_back_btn);
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, MapsActivity.class);
                finish();
            }
        });


        final ImageView mStationImage = findViewById(R.id.main_picture_view);
        final Place place = mPlace;
        try {
            // Get the photo metadata.
            PhotoMetadata photoMetadata = place.getPhotoMetadatas().get(0);

            // Get the attribution text.
            String attributions = photoMetadata.getAttributions();

            // Create a FetchPhotoRequest.
            FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
//                .setMaxWidth(500) // Optional.
//                .setMaxHeight(300) // Optional.
                    .build();
            mPlaceClient.fetchPhoto(photoRequest).addOnSuccessListener(new OnSuccessListener<FetchPhotoResponse>() {
                @Override
                public void onSuccess(FetchPhotoResponse fetchPhotoResponse) {
                    Bitmap bitmap = fetchPhotoResponse.getBitmap();
                    mStationImage.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        int statusCode = apiException.getStatusCode();
                        // Handle error with given status code.
                        Log.e(TAG, "Place not found: " + exception.getMessage());
                    }
                }
            });
        } catch (NullPointerException e) {
            https://icon-library.net/images/no-image-available-icon/no-image-available-icon-6.jpg
            Picasso.get().load("https://icon-library.net/images/no-image-available-icon/no-image-available-icon-6.jpg").into(mStationImage);
            Log.e(TAG, "onBindViewHolder: " + e.getMessage() );
        }

        Log.d(TAG, "onCreate: "+mPlace);

        TextView mStationName = findViewById(R.id.detail_name);
        mStationName.setText(mPlace.getName());
        TextView mStationURL = findViewById(R.id.detail_url);
        mStationURL.setText(mPlace.getWebsiteUri().toString());
        TextView mStationPrice = findViewById(R.id.detail_price);
        double price = getIntent().getDoubleExtra("ChargePrice", 0.00);
        mStationPrice.setText("$ " + String.valueOf(price) + " /kwh");
        TextView mStationAddress = findViewById(R.id.detail_address);
        mStationAddress.setText(mPlace.getAddress());
        StreetViewPanoramaFragment streetViewPanoramaFragment =
                (StreetViewPanoramaFragment) getFragmentManager()
                        .findFragmentById(R.id.streetview_small);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);
        RatingBar mRatingBar = findViewById(R.id.detail_rating_bar);
        mRatingBar.setRating((float)(mPlace.getRating()!=null? mPlace.getRating() : 5.0f));

    }

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
        streetViewPanorama.setPosition(mPlace.getLatLng());
    }
}
