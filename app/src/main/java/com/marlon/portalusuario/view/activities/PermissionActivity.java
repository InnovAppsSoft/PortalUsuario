package com.marlon.portalusuario.view.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.marlon.portalusuario.R;

import moe.feng.common.stepperview.IStepperAdapter;
import moe.feng.common.stepperview.VerticalStepperItemView;
import moe.feng.common.stepperview.VerticalStepperView;

public class PermissionActivity extends AppCompatActivity implements IStepperAdapter {
    private VerticalStepperView mVerticalStepperView ;
    int ResultCall = 1001;
    private static final int REQUEST_VPN = 1;
    private static final int REQUEST_OVERLAY = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        //
        mVerticalStepperView  = findViewById(R.id.vertical_stepper_view);
        mVerticalStepperView.setStepperAdapter(this);
    }

    @NonNull
    @Override
    public CharSequence getTitle(int index) {
        switch (index) {
            case 0:
                return "Permiso para realizar llamadas";
            case 1:
                return "Permiso para usar la cámara";
            case 2:
                return "Permiso para leer contactos";
            case 3:
                return "Permiso para obtener ubicación";
            case 4:
                return "Permiso de superposición";
            case 5:
                return "¡Permisos concedidos!";
            default:
                return "";
        }
    }

    @Nullable
    @Override
    public CharSequence getSummary(int index) {
        return null;
    }

    @Override
    public int size() {
        return 6;
    }

    @Override
    public View onCreateCustomView(int index, Context context, VerticalStepperItemView parent) {
        View inflateView = LayoutInflater.from(context).inflate(R.layout.vertical_stepper_sample_item, parent, false);
        TextView contentView = inflateView.findViewById(R.id.item_content);
        contentView.setText(
                index == 0 ? R.string.content_step_0 : (index == 1 ? R.string.content_step_1 : (index == 2 ? R.string.content_step_2 : (index == 3 ? R.string.content_step_3 : (index == 4 ? R.string.content_step_4 : R.string.content_step_7))))
        );
        Button nextButton = inflateView.findViewById(R.id.button_next);
        nextButton.setText(index == size() - 1 ? "Comenzar" : "Conceder");
        nextButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                switch (index){
                    case 0:
                        ActivityCompat.requestPermissions(PermissionActivity.this, new String[]{
                                Manifest.permission.CALL_PHONE}, ResultCall);
                        break;
                    case 1:
                        ActivityCompat.requestPermissions(PermissionActivity.this, new String[]{
                                Manifest.permission.CAMERA}, ResultCall);
                        break;
                    case 2:
                        ActivityCompat.requestPermissions(PermissionActivity.this, new String[]{
                                Manifest.permission.READ_CONTACTS}, ResultCall);
                        break;
                    case 3:
                        ActivityCompat.requestPermissions(PermissionActivity.this, new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE}, ResultCall);
                        break;
                    case 4:
                        startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), REQUEST_OVERLAY);
                        break;

                    default:
                        startActivity(new Intent(PermissionActivity.this, MainActivity.class));
                        finish();
                        break;
                }
//                    Snackbar.make(mVerticalStepperView, "Set!", Snackbar.LENGTH_LONG).show();
//                }
            }
        });
        Button prevButton = inflateView.findViewById(R.id.button_prev);
        prevButton.setVisibility(index == 4 ? View.VISIBLE : View.GONE);
        inflateView.findViewById(R.id.button_prev).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mVerticalStepperView.nextStep();
            }
        });
        return inflateView;
    }

    @Override
    public void onShow(int index) {

    }

    @Override
    public void onHide(int index) {

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == ResultCall){
            int idx = mVerticalStepperView.getCurrentStep();
            if (idx == 0) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    mVerticalStepperView.setErrorText(idx, "Sin este permiso la aplicación no puede funcionar");
                    return;
                }
            }else if (idx == 1){
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    mVerticalStepperView.setErrorText(idx, "Sin este permiso la aplicación no puede funcionar");
                    return;
                }
            }else if (idx == 2){
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    mVerticalStepperView.setErrorText(idx, "Sin este permiso la aplicación no puede funcionar");
                    return;
                }
            }else if (idx == 3){
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    mVerticalStepperView.setErrorText(idx, "Sin este permiso la aplicación no puede funcionar");
                    return;
                }
            }else if (idx >= 4){
                return;
            }
            mVerticalStepperView.setErrorText(idx, null);
            mVerticalStepperView.nextStep();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int idx = mVerticalStepperView.getCurrentStep();
        if (requestCode == REQUEST_OVERLAY){
            if(!Settings.canDrawOverlays(this)) {
                mVerticalStepperView.setErrorText(idx, "Permiso no concedido. Puede concederlo más adelante");
                return;
            }
        }
        mVerticalStepperView.setErrorText(idx, null);
        mVerticalStepperView.nextStep();
    }
}