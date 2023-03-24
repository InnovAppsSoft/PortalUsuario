package com.marlon.portalusuario.view.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.marlon.portalusuario.R;

import java.util.ArrayList;
import java.util.Random;

public class LlamadaOcultoActivity extends AppCompatActivity {

    private String numero_llamar = "";
    private String data = "";

    private Intent i = new Intent();

    @Override
    protected void onCreate(Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);
        setContentView(R.layout.activity_llamada_oculto);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CALL_PHONE}, 1000);
        }
        else {
            initializeLogic();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            initializeLogic();
        }
    }

    private void initializeLogic() {
        _seleccionar_contacto();
		/*
}

@Override
protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {
super.onActivityResult(_requestCode, _resultCode, _data);

switch (_requestCode) {

default:
break;
}
}

@Override
public void onStart() {
super.onStart();
*/
    }
    private void _seleccionar_contacto () {
        Intent ic = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        ic.setType(android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(ic,PICK_CONTACT_REQUEST);
    }
    static final int PICK_CONTACT_REQUEST = 1;
    String ca0,ca1,ca2,ca3,ca4,ca5,ca6,ca7,ca8,ca9,ca10,ca11,ca12,ca13,ca14,ca15;
    String as0,as1,as2,as3,as4,as12,as13;
    String qwe = "";
    String union = "";
    String error = "Numero erroneo";
    int cuantos_caracteres;
    String error2 = "            Cuidado ..\n Faltan caracteres o su número seleccionado no es un número de telefonia móvil  ";
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
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
                            numero_llamar = union;
                        } else {
                            Toast.makeText(LlamadaOcultoActivity.this, error, Toast.LENGTH_SHORT).show();
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
                                    numero_llamar = nuu;
                                } else {
                                    Toast.makeText(LlamadaOcultoActivity.this, error, Toast.LENGTH_SHORT).show();
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
                                            numero_llamar = nuu;
                                        } else {
                                            Toast.makeText(LlamadaOcultoActivity.this, error, Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(LlamadaOcultoActivity.this, error, Toast.LENGTH_SHORT).show();
                                    }


                                } else {

                                    if (cuantos_caracteres < 8) {

                                        Toast.makeText(LlamadaOcultoActivity.this, error2, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(LlamadaOcultoActivity.this, error, Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }

                        } else {

                            Toast.makeText(LlamadaOcultoActivity.this, error, Toast.LENGTH_SHORT).show();

                        }

                    }

                    //995-355-55060199
                    //+535 5550601
                    //55550601
                    //textnombre.setText(nombre);
                    // editnumero.setText(numero);
                    //    }
                    //    }

                    //    }
                    if (numero_llamar.equals("")) {

                    } else {
                        i.setAction(Intent.ACTION_CALL);
                        i.setData(Uri.parse("tel:%2331%23" + numero_llamar));
                        startActivity(i);
                    }
                }
            }
        }
        finish();
    }


    @Deprecated
    public void showMessage(String _s) {
        Toast.makeText(getApplicationContext(), _s, Toast.LENGTH_SHORT).show();
    }

    @Deprecated
    public int getLocationX(View _v) {
        int _location[] = new int[2];
        _v.getLocationInWindow(_location);
        return _location[0];
    }

    @Deprecated
    public int getLocationY(View _v) {
        int _location[] = new int[2];
        _v.getLocationInWindow(_location);
        return _location[1];
    }

    @Deprecated
    public int getRandom(int _min, int _max) {
        Random random = new Random();
        return random.nextInt(_max - _min + 1) + _min;
    }

    @Deprecated
    public ArrayList<Double> getCheckedItemPositionsToArray(ListView _list) {
        ArrayList<Double> _result = new ArrayList<Double>();
        SparseBooleanArray _arr = _list.getCheckedItemPositions();
        for (int _iIdx = 0; _iIdx < _arr.size(); _iIdx++) {
            if (_arr.valueAt(_iIdx))
                _result.add((double)_arr.keyAt(_iIdx));
        }
        return _result;
    }

    @Deprecated
    public float getDip(int _input){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, _input, getResources().getDisplayMetrics());
    }

    @Deprecated
    public int getDisplayWidthPixels(){
        return getResources().getDisplayMetrics().widthPixels;
    }

    @Deprecated
    public int getDisplayHeightPixels(){
        return getResources().getDisplayMetrics().heightPixels;
    }

}

