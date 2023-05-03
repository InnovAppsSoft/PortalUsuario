package com.marlon.portalusuario.perfil;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.elevation.SurfaceColors;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.marlon.portalusuario.R;
import com.marlon.portalusuario.view.fragments.CuentasFragment;
import com.marlon.portalusuario.activities.MainActivity;

import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilActivity extends AppCompatActivity {
    SharedPreferences sp_perfil;
    SharedPreferences.Editor editor;
    Menu optionMenu;

    CircleImageView imgperfil;
    TextInputEditText nameperfil,numeroperfil,perfilnauta;
    CollapsingToolbarLayout UsuarioName;
    Button guardarbtn;
    FloatingActionButton Add,Eliminar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.marlon.portalusuario.R.layout.activity_perfil);

        imgperfil = findViewById(R.id.image_perfil);
        nameperfil = findViewById(R.id.edit_perfil_nombre);
        numeroperfil = findViewById(R.id.edit_perfil_numero);
        perfilnauta = findViewById(R.id.edit_perfil_nauta);
        guardarbtn = findViewById(R.id.buttom_save_perfil);
        Add = findViewById(R.id.fab);
        UsuarioName = findViewById(R.id.collapsingTbl);
        Eliminar = findViewById(R.id.eliminar);


        // TODO: SharedPreferences info perfil
        sp_perfil = getSharedPreferences("profile", Context.MODE_PRIVATE);
        editor = sp_perfil.edit();

        // TODO: select picture of profile
        Add.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        select_profile();
                    }
                });

        // TODO: load pic
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // solicitar si no
                ActivityCompat.requestPermissions(
                        this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 20);
            } else {
                Bitmap load =
                        new ImageSaver(getApplicationContext())
                                .setExternal(true)
                                .setFileName("IMG.png")
                                .setDirectoryName("PortalUsuario")
                                .load();
                if (load == null) {
                    imgperfil.setImageResource(R.drawable.portal);
                } else {
                   imgperfil.setImageBitmap(load);
                }
            }
        } else {
            // SDK > 29 ejecutar
            Bitmap load =
                    new ImageSaver(getApplicationContext())
                            .setFileName("IMG.png")
                            .setDirectoryName("PortalUsuario")
                            .load();
            if (load == null) {
                imgperfil.setImageResource(R.drawable.portal);
            } else {
                imgperfil.setImageBitmap(load);
            }
        }

        // TODO:  Save info
        nameperfil.setText(sp_perfil.getString("nombre", "").toString());
        numeroperfil.setText(sp_perfil.getString("numero", "").toString());
        perfilnauta.setText(sp_perfil.getString("nauta", "").toString());
        UsuarioName.setTitle(sp_perfil.getString("nombre", "Usuario").toString());
        // buttom
        guardarbtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        Snackbar.make(arg0, "Guardado con exito", Snackbar.LENGTH_LONG).show();

                        editor.putString(
                                "nombre", UsuarioName.getTitle().toString().trim());
                        editor.putString(
                                "nombre", nameperfil.getText().toString().trim());
                        editor.putString(
                                "numero",numeroperfil.getText().toString().trim());
                        editor.putString(
                                "nauta", perfilnauta.getText().toString().trim());
                        editor.commit();
                    }


                });


        Eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePerfil();
            }
        });
    }





    private void select_profile() {
        if (Build.VERSION.SDK_INT < 32) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // solicitar permisosn
                if (ContextCompat.checkSelfPermission(
                        this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 22);
                } else {
                    // esta dado, continuar
                    getPicture.launch("image/*");
                }

            } else {
                // < VERAION_CODES.M no solicitar permiso continuar
                getPicture.launch("image/*");
            }
        } else {
            // SDK 32, no solicitar permisos y continuar
            getPicture.launch("image/*");
        }
    }

    private void deletePerfil() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            new ImageSaver(getApplicationContext())
                    .setFileName("IMG.png")
                    .setDirectoryName("PortalUsuario")
                    .deleteFile();
            imgperfil.setImageResource(R.drawable.portal);
        } else {
            new ImageSaver(getApplicationContext())
                    .setExternal(true)
                    .setFileName("IMG.png")
                    .setDirectoryName("PortalUsuario")
                    .deleteFile();
           imgperfil.setImageResource(R.drawable.portal);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void saveImage(String fileName, String directory, Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            new ImageSaver(getApplicationContext())
                    .setFileName(fileName)
                    .setDirectoryName(directory)
                    .save(bitmap);
        } else {
            new ImageSaver(getApplicationContext())
                    .setFileName(fileName)
                    .setDirectoryName(directory)
                    .setExternal(true)
                    .save(bitmap);
        }
    }

    ActivityResultLauncher<String> getPicture =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    new ActivityResultCallback<Uri>() {
                        @Override
                        public void onActivityResult(Uri uri) {
                            if (uri != null) {
                                try {
                                    if (Build.VERSION.SDK_INT < 29) {
                                        Bitmap bitmap =
                                                MediaStore.Images.Media.getBitmap(
                                                        PerfilActivity.this.getContentResolver(),
                                                        uri);
                                        saveImage("IMG.png", "PortalUsuario", bitmap);
                                        imgperfil.setImageBitmap(bitmap);
                                    } else {
                                        ImageDecoder.Source source =
                                                ImageDecoder.createSource(
                                                        PerfilActivity.this.getContentResolver(),
                                                        uri);
                                        Bitmap bitmap = ImageDecoder.decodeBitmap(source);
                                        saveImage("IMG.png", "PortalUsuario", bitmap);
                                        imgperfil.setImageBitmap(bitmap);
                                    }

                                } catch (IOException e) {

                                }
                            }
                        }
                    });
}