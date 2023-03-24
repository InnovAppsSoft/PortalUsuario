package com.marlon.portalusuario.view.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.marlon.portalusuario.R;

public class BottomSheetDialog extends BottomSheetDialogFragment {
    private BottomSheetListener bottomSheetListener;
    private LinearLayout toolsBtn;
    private LinearLayout settingsBtn;
    private LinearLayout donateBtn;
    private LinearLayout moreBtn;
    private LinearLayout aboutBtn;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_dialog_main_activity_layout, container, false);
        // Referencing
        toolsBtn = v.findViewById(R.id.tools_btn);
        settingsBtn = v.findViewById(R.id.settings_btn);
        donateBtn = v.findViewById(R.id.donate_btn);
        moreBtn = v.findViewById(R.id.more_btn);
        aboutBtn = v.findViewById(R.id.about_btn);
        // Listeners
        toolsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetListener.onButtonClicked("tools");
            }
        });
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetListener.onButtonClicked("settings");
            }
        });
        donateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetListener.onButtonClicked("donate");
            }
        });
        moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetListener.onButtonClicked("more");
            }
        });
        aboutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetListener.onButtonClicked("about");
            }
        });
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
