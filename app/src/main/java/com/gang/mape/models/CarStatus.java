package com.gang.mape.models;

import android.util.Log;

import com.gang.mape.R;

import java.util.HashMap;

public class CarStatus {

    private static final String TAG = "CarStatus";

    public static CarStatus instance;

    private String mCarModel;
    private int mBattery = 70;

    private HashMap<String, Integer> ModelImageId = new HashMap<>();

    public CarStatus(String carModel, int battery) {
        mCarModel = carModel;
        mBattery = battery;
    }

    public CarStatus(){
        ModelImageId.put("Tesla Model S", R.drawable.tesla);
        ModelImageId.put("Nissan Leaf", R.drawable.nissan);
        ModelImageId.put("Mercedes-Benz EQC", R.drawable.benz);
        ModelImageId.put("BMW i3", R.drawable.bmw);
        ModelImageId.put("NONE", R.drawable.question_car);
    }

    public static CarStatus getInstance() {
        if (instance == null) {
            instance = new CarStatus();
        }
        return instance;
    }

    public String getCarModel() {
        return mCarModel;
    }

    public void setCarModel(String carModel) {
        mCarModel = carModel;
    }

    public int getBattery() {
        return mBattery;
    }

    public void setBattery(int battery) {
        mBattery = battery;
    }

    public int getModelImage(){
        if (mCarModel == "Other") return ModelImageId.get("NONE");
        try {
            return ModelImageId.get(mCarModel);
        } catch(Exception e){
            Log.d(TAG, "getModelImage: " + e.getMessage());
        }
        return ModelImageId.get("NONE");
    }
}
