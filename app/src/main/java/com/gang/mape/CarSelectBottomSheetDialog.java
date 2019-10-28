package com.gang.mape;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gang.mape.models.CarStatus;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CarSelectBottomSheetDialog extends BottomSheetDialogFragment {
    private BottomSheetListener mListener;

    private CarStatus myCar;

    private Button mClearBtn;
    private Button mConfirmBtn;
    private Spinner spinner1;
    private SeekBar mBatterSeekBar;
    private TextView mBatteryText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_select_car, container, false);
        v.setClipToOutline(true);

        myCar = CarStatus.getInstance();

        spinner1 = (Spinner) v.findViewById(R.id.spinner1);
        final String[] modelStrings = getResources().getStringArray(R.array.CarModels);

        ArrayAdapter<String> langAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_text, Arrays.asList(modelStrings)){
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    tv.setTextColor(getResources().getColor(R.color.colorAccentTrans));
                } else {
                    tv.setTextColor(getResources().getColor(R.color.colorAccent));
                }
                return view;
            }

            @Override
            public boolean isEnabled(int position) {
                if(position == 0) {
                    return false;
                } else {
                    return true;
                }
            }
        };
        langAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        spinner1.setAdapter(langAdapter);

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                if(position>0){
                    Toast.makeText(getContext(), "Your model is " + selectedItemText, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mBatterSeekBar = v.findViewById(R.id.battery_seekBar);
        mBatterSeekBar.setProgress(myCar.getBattery());
        mBatteryText = v.findViewById(R.id.battery_text);
        mBatteryText.setText(String.valueOf(myCar.getBattery())+"%");
        mBatterSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mBatteryText.setText(String.valueOf(progress) + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mClearBtn = v.findViewById(R.id.btn_clear);
        mClearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myCar.setBattery(0);
                myCar.setCarModel("NONE");
                mBatterSeekBar.setProgress(0);
                spinner1.setSelection(0);
            }
        });

        mConfirmBtn = v.findViewById(R.id.btn_confirm);
        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spinner1.getSelectedItemPosition() == 0){
                    myCar.setCarModel("NONE");
                    myCar.setBattery(0);
                    Toast.makeText(getContext(), "Please Select Car Model", Toast.LENGTH_SHORT).show();
                } else {
                    myCar.setCarModel(spinner1.getSelectedItem().toString());
                    myCar.setBattery(mBatterSeekBar.getProgress());
                    mListener.onCarSelected(myCar);
                    dismiss();
                }
            }
        });

        return v;
    }

    public interface BottomSheetListener {
        void onCarSelected(CarStatus instance);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try{
            mListener = (BottomSheetListener) context;
        } catch (ClassCastException e){
            throw new ClassCastException(context.toString() + " must implement BottomSheetListener");
        }
    }
}
