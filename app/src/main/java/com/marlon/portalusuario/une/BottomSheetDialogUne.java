package com.marlon.portalusuario.une;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.marlon.portalusuario.R;


public class BottomSheetDialogUne extends BottomSheetDialogFragment {
    private BottomSheetListener bottomSheetListener;
    private TextView totalConsumption;
    private TextView totalToPay;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_une, container, false);
        // Referencing
        totalConsumption = v.findViewById(R.id.total_consumption);
        totalToPay = v.findViewById(R.id.total_to_pay);
        // Listeners
        double [] resumenUne = {};
//        totalConsumption.setText("Consumo total: " + String.valueOf(resumenUne[2]) + "kW/h");
//        totalToPay.setText("Importe total: $" + String.valueOf(resumenUne[3]));
        //
        return v;
    }

    public interface BottomSheetListener{
        void onButtonClicked(String text);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            bottomSheetListener = (BottomSheetListener) context;
        }catch (ClassCastException ex){
            ex.printStackTrace();
        }
    }
}
