package com.gang.mape;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.gang.mape.R;
import com.gang.mape.models.PlaceInfo;
import com.gang.mape.models.StationModel;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPhotoResponse;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class StationRecyclerViewAdapter extends RecyclerView.Adapter<StationRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "StationAdapter";
    private ArrayList<StationModel> mStations;
    private BottomSheetBehavior mBottomSheetBehavior;
    private Context mContext;
    private PlacesClient mPlaceClient;


    public StationRecyclerViewAdapter(BottomSheetBehavior bottomSheetBehavior, Context context, ArrayList<StationModel> stations){
        Log.d(TAG, "StationRecyclerViewAdapter: constructed");
        this.mStations = stations;
        this.mBottomSheetBehavior = bottomSheetBehavior;
        this.mContext = context;
        this.mPlaceClient = Places.createClient(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        // Log.d(TAG, "Add item - onBindViewHolder called"+mStations.get(position).getPlaceHandle().getWebsiteUri());
        final Place place = mStations.get(position).getPlaceHandle();
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
                    holder.mIconView.setImageBitmap(bitmap);
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
            Picasso.get().load(mStations.get(position).getIcon()).into(holder.mIconView);
            Log.e(TAG, "onBindViewHolder: " + e.getMessage() );
        }
        Random rand = new Random();
        final double price = (double) Math.round((3.0d + rand.nextDouble()/3.0d)/3.5*100)/100;
        holder.mNameView.setText(mStations.get(position).getName());
        try { holder.mAddressView.setText("Addr: " + mStations.get(position).getVicinity()); }
        catch (NullPointerException e) { holder.mAddressView.setText("No address Info");}
        try { holder.mPriceView.setText("$ " + price); }
        catch (NullPointerException e) {holder.mPriceView.setText("N/A");}
        try {holder.mUrlView.setText("Rating: " + place.getRating().toString());}
        catch (NullPointerException e) {holder.mUrlView.setText("N/A");
            Log.e(TAG, "onBindViewHolder: " + e.getMessage());}
        holder.mOpenView.setText("OPEN");
        holder.mMoreInfoBtnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, mStations.get(position).getName() + "Detail", Toast.LENGTH_SHORT).show();
                Intent myIntent = new Intent(mContext, DetailActivity.class);

                myIntent.putExtra("StationDetail", (Parcelable) place);
                myIntent.putExtra("ChargePrice", price);
                mContext.startActivity(myIntent);
            }
        });
        holder.mConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStations.get(position).moveToMe();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mStations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mIconView;
        TextView  mNameView;
        TextView mAddressView;
        TextView mPriceView;
        TextView mUrlView;
        Button mMoreInfoBtnView;
        TextView mOpenView;
        ConstraintLayout mConstraintLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mIconView = itemView.findViewById(R.id.station_icon);
            mNameView  = itemView.findViewById(R.id.station_name);
            mAddressView = itemView.findViewById(R.id.station_address);
            mPriceView = itemView.findViewById(R.id.station_price);
            mUrlView = itemView.findViewById(R.id.station_url);
            mMoreInfoBtnView = itemView.findViewById(R.id.btn_more_info);
            mOpenView = itemView.findViewById(R.id.station_status);
            mConstraintLayout = itemView.findViewById(R.id.station_recycle_item_layout);
        }
    }

}
