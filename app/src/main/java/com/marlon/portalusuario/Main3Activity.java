package com.marlon.portalusuario;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Main3Activity extends AppCompatActivity {

    private static Context context;

    int ResultCall = 1001;

    private static final String SELECTED_MENU = "selected_menu";
    BottomNavigationView navView;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = item -> {
        Fragment fragment = null;
        if (item.getItemId() == R.id.servicios) {
            fragment = ServiciosFragment.newInstance(context);
        } else if (item.getItemId() == R.id.paquete) {
            fragment = PaquetesFragment.newInstance(context);
        } else if (item.getItemId() == R.id.wifi) {
            fragment = WifiFragment.newInstance(context);
        } else if (item.getItemId() == R.id.web) {
            fragment = WebFragment.newInstance(context);
        } else if (item.getItemId() == R.id.confi) {
            fragment = ConfiguracionFragment.newInstance(context);
        }

        if (fragment != null) {
            this.getSupportFragmentManager()
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .replace(R.id.fragmentContainer, fragment)
                    .commit();
        }
        return true;
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            PermisoLlamada();
        }

        context = Main3Activity.this;

        navView = findViewById(R.id.bottomNavigation);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if (savedInstanceState != null) {
            savedInstanceState.getInt(SELECTED_MENU);
        } else {
            navView.setSelectedItemId(R.id.servicios);
        }

    }



    public void showMessage(Context c, String _s) {
        Toast.makeText(c, _s, Toast.LENGTH_SHORT).show();
    }


    public void PermisoLlamada(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED

        ){
            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle("Permisos Necesarios");
            dialogo1.setMessage(R.string.main_dialog_permissions);
            dialogo1.setIcon(R.drawable.portal);
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(Main3Activity.this, new String[]{
                            Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.CALL_PHONE, Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.CALL_PHONE, Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.CALL_PHONE, Manifest.permission.CAMERA,
                            Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.CALL_PHONE, Manifest.permission.READ_CONTACTS}, ResultCall);

                }

            });

            dialogo1.create().show();

        }
    }

    static final int PICK_CONTACT_REQUEST = 1;
    String ca0, ca1, ca2, ca3, ca4, ca5, ca6, ca7, ca8, ca9, ca10, ca11, ca12, ca13, ca14, ca15;
    String as0, as1, as2, as3, as4, as12, as13;
    String qwe = "";
    String union = "";
    String error = "Numero erroneo";
    int cuantos_caracteres;
    String error2 = "            Cuidado ..\n Faltan caracteres o su número seleccionado no es un número de telefonia móvil  ";


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode,resultCode,data);
        }
        if (requestCode == PICK_CONTACT_REQUEST) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();

                Cursor cursor = getContentResolver().query(uri, null, null, null, null);

                if (cursor.moveToFirst()) {
                    int columnaNombre = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                    int columnaNumero = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                    String nombre = cursor.getString(columnaNombre);
                    String numero = cursor.getString(columnaNumero);

                    union = "";
                    if (numero.length() > 0) {
                        ca0 = "" + numero.charAt(0) + "";
                        qwe = ca0;
                        if (qwe.equals("0") || qwe.equals("1") || qwe.equals("2") || qwe.equals("3") || qwe.equals("4") || qwe.equals("5") || qwe.equals("6") || qwe.equals("7") || qwe.equals("8") || qwe.equals("9")) {
                            union += qwe;
                        }

                        if (numero.length() > 1) {
                            ca1 = "" + numero.charAt(1) + "";
                            qwe = ca1;
                            if (qwe.equals("0") || qwe.equals("1") || qwe.equals("2") || qwe.equals("3") || qwe.equals("4") || qwe.equals("5") || qwe.equals("6") || qwe.equals("7") || qwe.equals("8") || qwe.equals("9")) {
                                union += qwe;
                            }
                            if (numero.length() > 2) {
                                ca2 = "" + numero.charAt(2) + "";
                                qwe = ca2;
                                if (qwe.equals("0") || qwe.equals("1") || qwe.equals("2") || qwe.equals("3") || qwe.equals("4") || qwe.equals("5") || qwe.equals("6") || qwe.equals("7") || qwe.equals("8") || qwe.equals("9")) {
                                    union += qwe;
                                }
                                if (numero.length() > 3) {
                                    ca3 = "" + numero.charAt(3) + "";
                                    qwe = ca3;
                                    if (qwe.equals("0") || qwe.equals("1") || qwe.equals("2") || qwe.equals("3") || qwe.equals("4") || qwe.equals("5") || qwe.equals("6") || qwe.equals("7") || qwe.equals("8") || qwe.equals("9")) {
                                        union += qwe;
                                    }
                                    if (numero.length() > 4) {
                                        ca4 = "" + numero.charAt(4) + "";
                                        qwe = ca4;
                                        if (qwe.equals("0") || qwe.equals("1") || qwe.equals("2") || qwe.equals("3") || qwe.equals("4") || qwe.equals("5") || qwe.equals("6") || qwe.equals("7") || qwe.equals("8") || qwe.equals("9")) {
                                            union += qwe;
                                        }
                                        if (numero.length() > 5) {
                                            ca5 = "" + numero.charAt(5) + "";
                                            qwe = ca5;
                                            if (qwe.equals("0") || qwe.equals("1") || qwe.equals("2") || qwe.equals("3") || qwe.equals("4") || qwe.equals("5") || qwe.equals("6") || qwe.equals("7") || qwe.equals("8") || qwe.equals("9")) {
                                                union += qwe;
                                            }
                                            if (numero.length() > 6) {
                                                ca6 = "" + numero.charAt(6) + "";
                                                qwe = ca6;
                                                if (qwe.equals("0") || qwe.equals("1") || qwe.equals("2") || qwe.equals("3") || qwe.equals("4") || qwe.equals("5") || qwe.equals("6") || qwe.equals("7") || qwe.equals("8") || qwe.equals("9")) {
                                                    union += qwe;
                                                }
                                                if (numero.length() > 7) {
                                                    ca7 = "" + numero.charAt(7) + "";
                                                    qwe = ca7;
                                                    if (qwe.equals("0") || qwe.equals("1") || qwe.equals("2") || qwe.equals("3") || qwe.equals("4") || qwe.equals("5") || qwe.equals("6") || qwe.equals("7") || qwe.equals("8") || qwe.equals("9")) {
                                                        union += qwe;
                                                    }
                                                    if (numero.length() > 8) {
                                                        ca8 = "" + numero.charAt(8) + "";
                                                        qwe = ca8;
                                                        if (qwe.equals("0") || qwe.equals("1") || qwe.equals("2") || qwe.equals("3") || qwe.equals("4") || qwe.equals("5") || qwe.equals("6") || qwe.equals("7") || qwe.equals("8") || qwe.equals("9")) {
                                                            union += qwe;
                                                        }
                                                        if (numero.length() > 9) {
                                                            ca9 = "" + numero.charAt(9) + "";
                                                            qwe = ca9;
                                                            if (qwe.equals("0") || qwe.equals("1") || qwe.equals("2") || qwe.equals("3") || qwe.equals("4") || qwe.equals("5") || qwe.equals("6") || qwe.equals("7") || qwe.equals("8") || qwe.equals("9")) {
                                                                union += qwe;
                                                            }
                                                            if (numero.length() > 10) {
                                                                ca10 = "" + numero.charAt(10) + "";
                                                                qwe = ca10;
                                                                if (qwe.equals("0") || qwe.equals("1") || qwe.equals("2") || qwe.equals("3") || qwe.equals("4") || qwe.equals("5") || qwe.equals("6") || qwe.equals("7") || qwe.equals("8") || qwe.equals("9")) {
                                                                    union += qwe;
                                                                }
                                                                if (numero.length() > 11) {
                                                                    ca11 = "" + numero.charAt(11) + "";
                                                                    qwe = ca11;
                                                                    if (qwe.equals("0") || qwe.equals("1") || qwe.equals("2") || qwe.equals("3") || qwe.equals("4") || qwe.equals("5") || qwe.equals("6") || qwe.equals("7") || qwe.equals("8") || qwe.equals("9")) {
                                                                        union += qwe;
                                                                    }
                                                                    if (numero.length() > 12) {
                                                                        ca12 = "" + numero.charAt(12) + "";
                                                                        qwe = ca12;
                                                                        if (qwe.equals("0") || qwe.equals("1") || qwe.equals("2") || qwe.equals("3") || qwe.equals("4") || qwe.equals("5") || qwe.equals("6") || qwe.equals("7") || qwe.equals("8") || qwe.equals("9")) {
                                                                            union += qwe;
                                                                        }
                                                                        if (numero.length() > 13) {
                                                                            ca13 = "" + numero.charAt(13) + "";
                                                                            qwe = ca13;
                                                                            if (qwe.equals("0") || qwe.equals("1") || qwe.equals("2") || qwe.equals("3") || qwe.equals("4") || qwe.equals("5") || qwe.equals("6") || qwe.equals("7") || qwe.equals("8") || qwe.equals("9")) {
                                                                                union += qwe;
                                                                            }
                                                                            if (numero.length() > 14) {
                                                                                ca14 = "" + numero.charAt(14) + "";
                                                                                qwe = ca14;
                                                                                if (qwe.equals("0") || qwe.equals("1") || qwe.equals("2") || qwe.equals("3") || qwe.equals("4") || qwe.equals("5") || qwe.equals("6") || qwe.equals("7") || qwe.equals("8") || qwe.equals("9")) {
                                                                                    union += qwe;
                                                                                }
                                                                                if (numero.length() > 15) {
                                                                                    ca15 = "" + numero.charAt(15) + "";
                                                                                    qwe = ca15;
                                                                                    if (qwe.equals("0") || qwe.equals("1") || qwe.equals("2") || qwe.equals("3") || qwe.equals("4") || qwe.equals("5") || qwe.equals("6") || qwe.equals("7") || qwe.equals("8") || qwe.equals("9")) {
                                                                                        union += qwe;
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                    }

                    if (union.length() == 8) {
                        String ewq = "" + union.charAt(0) + "";
                        if (ewq.equals("5")) {
                            ServiciosFragment.t.setText(union);
                        } else {
                            showMessage(getApplicationContext(), error);
                        }

                    } else {

                        if (union.length() < 15) {
                            cuantos_caracteres = union.length();
                            if (cuantos_caracteres == 14) {
                                as0 = "" + union.charAt(0) + "";
                                as1 = "" + union.charAt(1) + "";
                                as2 = "" + union.charAt(2) + "";
                                as3 = "" + union.charAt(3) + "";
                                as4 = "" + union.charAt(4) + "";
                                as12 = "" + union.charAt(12) + "";
                                as13 = "" + union.charAt(13) + "";
                                if (as0.equals("9") && as1.equals("9") && as2.equals("5") && as3.equals("3") && as4.equals("5") && as12.equals("9") && as13.equals("9")) {
                                    String nuu = "" + union.charAt(4) + union.charAt(5) + union.charAt(6) + union.charAt(7) + union.charAt(8) + union.charAt(9) + union.charAt(10) + union.charAt(11) + "";
                                    ServiciosFragment.t.setText(nuu);
                                } else {
                                    showMessage(getApplicationContext(), error);
                                }
                            } else {
                                cuantos_caracteres = union.length();
                                if (cuantos_caracteres == 10) {
                                    as0 = "" + union.charAt(0) + "";
                                    as1 = "" + union.charAt(1) + "";
                                    as2 = "" + union.charAt(2) + "";
                                    if ((as0.equals("5") && as1.equals("3")) || (as0.equals("9") && as1.equals("9"))) {
                                        if (as2.equals("5")) {
                                            String nuu = "" + union.charAt(2) + union.charAt(3) + union.charAt(4) + union.charAt(5) + union.charAt(6) + union.charAt(7) + union.charAt(8) + union.charAt(9) + "";
                                            ServiciosFragment.t.setText(nuu);
                                        } else {
                                            showMessage(getApplicationContext(), error);
                                        }
                                    } else {
                                        showMessage(getApplicationContext(), error);
                                    }


                                } else {

                                    if (cuantos_caracteres < 8) {
                                        ServiciosFragment.t.setText(union);
                                        showMessage(getApplicationContext(), error2);
                                    } else {
                                        showMessage(getApplicationContext(), error);
                                    }
                                }

                            }

                        } else {

                            showMessage(getApplicationContext(), error);


                        }

                    }


                    //textnombre.setText(nombre);
                    // editnumero.setText(numero);
                }
            }

        }
    }

    public void onResume() {
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == ResultCall){

            PermisoLlamada();

        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_MENU, navView.getSelectedItemId());
    }

}






