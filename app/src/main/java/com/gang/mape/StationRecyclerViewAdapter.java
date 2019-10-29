package com.gang.mape;

import android.content.Context;
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
import com.gang.mape.models.StationModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class StationRecyclerViewAdapter extends RecyclerView.Adapter<StationRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "StationAdapter";
    private ArrayList<StationModel> mStations;
    private BottomSheetBehavior mBottomSheetBehavior;
    private Context mContext;


    public StationRecyclerViewAdapter(BottomSheetBehavior bottomSheetBehavior, Context context, ArrayList<StationModel> stations){
        Log.d(TAG, "StationRecyclerViewAdapter: constructed");
        this.mStations = stations;
        this.mBottomSheetBehavior = bottomSheetBehavior;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "Add item - onBindViewHolder called"+mStations.get(position).getPlaceHandle().getWebsiteUri());
        Picasso.get().load(mStations.get(position).getIcon()).into(holder.mIconView);
        holder.mNameView.setText(mStations.get(position).getName());
        holder.mAddressView.setText(mStations.get(position).getVicinity());
        holder.mPriceView.setText("$10place");
        try{holder.mUrlView.setText(mStations.get(position).getPlaceHandle().getWebsiteUri().toString());}
        catch (NullPointerException e) {holder.mUrlView.setText("N/A");
            Log.e(TAG, "onBindViewHolder: " + e.getMessage());}
        holder.mOpenView.setText("OPEN");
        holder.mMoreInfoBtnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, mStations.get(position).getName() + "Detail", Toast.LENGTH_SHORT).show();
            }
        });
        holder.mConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(mContext, "Shared you stats with " + mStations.get(position), Toast.LENGTH_SHORT).show();
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
