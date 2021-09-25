package com.marlon.portalusuario;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.tasks.OnSuccessListener;

public class ActivityCell extends AppCompatActivity {

    private Button set4GBtn;
    private CountDownTimer x;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cell);

        set4GBtn = (Button) findViewById(R.id.set_4g);


        set4GBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCell.this.openHiddenMenu();
            }
        });

    }

    public void openHiddenMenu() {
        try {
            Intent intent = new Intent("android.intent.action.MAIN");
            if (Build.VERSION.SDK_INT >= 30) {
                intent.setClassName("com.android.phone", "com.android.phone.settings.RadioInfo");
            } else {
                intent.setClassName("com.android.settings", "com.android.settings.RadioInfo");
            }
            startActivity(intent);
        } catch (Exception unused) {
            Toast.makeText(getApplication(), "Su dispositivo no es compatible con esta aplicación, lamentamos las molestias :(", Toast.LENGTH_SHORT).show();
        }
    }
}