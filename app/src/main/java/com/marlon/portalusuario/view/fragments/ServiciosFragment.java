package com.marlon.portalusuario.view.fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.datatransport.runtime.logging.Logging;
import com.marlon.portalusuario.view.activities.EmergenciaActivity;
import com.marlon.portalusuario.view.activities.LlamadaOcultoActivity;
import com.marlon.portalusuario.view.activities.Llamada_99Activity;
import com.marlon.portalusuario.R;
import com.marlon.portalusuario.view.activities.SmsActivity;
import com.marlon.portalusuario.view.activities.VozActivity;
import com.marlon.portalusuario.codescanner.ScannerActivity;
import com.marlon.portalusuario.logging.JCLogging;
import com.marlon.portalusuario.senal.AppConfiguracionTool;
import com.marlon.portalusuario.util.Util;

public class ServiciosFragment<b> extends Fragment {

    private static Context context;

    private CardView activar,adicionar,consultar;
    private SharedPreferences salva,nochesalva;

    private CardView SMS, VOZ, PlanAmigos1, Emergencia;
    private RelativeLayout creditBtn, bonusBtn, mobileDataBtn, callPrivateBtn, call99Btn, voicePlansBtn, friendsPlanBtn, smsPlansBtn;
    private EditText donateMount, donateKey, rechargeCode, adelantaSaldoMount;
    public static EditText phoneNumber;
    private ImageView sendDonation, adelantaSaldoBtn, rechargeBtn, contactSBtn, scanQRRechargeCode;

    private TelephonyManager j;
    private TextView networkClass,Fecha;
    private TelephonyManager telephonyManager;
    private TextView tvSignal;
    private com.marlon.portalusuario.Utils utils;
    private Util util;

    private JCLogging logging;

    static final int PICK_CONTACT_REQUEST = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_servicios, container, false);

        util = new Util();
        logging = new JCLogging(getActivity());
        // ui components init

        SMS = view.findViewById(R.id.sms);
        VOZ = view.findViewById(R.id.voz);
        PlanAmigos1 =  view.findViewById(R.id.PlanAmigos1);
        Emergencia = view.findViewById(R.id.emergancia);

        phoneNumber = view.findViewById(R.id.telefono);
        donateMount = view.findViewById(R.id.donation_mount);
        donateKey = view.findViewById(R.id.donation_key);
        sendDonation = view.findViewById(R.id.Enviar);
        //
        rechargeCode = view.findViewById(R.id.recargartelefono);
        rechargeBtn = view.findViewById(R.id.Enviar1);
        scanQRRechargeCode = view.findViewById(R.id.camera);
        //
        adelantaSaldoMount = view.findViewById(R.id.adesaldo);
        adelantaSaldoBtn = view.findViewById(R.id.adelantar);

        creditBtn = view.findViewById(R.id.saldo);
        bonusBtn = view.findViewById(R.id.bono);
        mobileDataBtn = view.findViewById(R.id.megas);
        call99Btn = view.findViewById(R.id.llamar99);
        callPrivateBtn = view.findViewById(R.id.llamarPrivado);
        contactSBtn = view.findViewById(R.id.Contactos);
        voicePlansBtn = view.findViewById(R.id.microfono);
        friendsPlanBtn = view.findViewById(R.id.amigosplan);
        smsPlansBtn = view.findViewById(R.id.smsplan);


        friendsPlanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                USSDcall("*222*264%23");
            }
        });

        smsPlansBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                USSDcall("*222*767%23");
            }
        });

        voicePlansBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                USSDcall("*222*869%23");
            }
        });

        scanQRRechargeCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              startActivityForResult(new Intent(getContext(), ScannerActivity.class),6932);

            }
        });


        contactSBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickContact();
            }
        });

        call99Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext().startActivity(new Intent(getContext(), Llamada_99Activity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        callPrivateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext().startActivity(new Intent(getContext(), LlamadaOcultoActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }

        });

        creditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                USSDcall("*222%23");
            }
        });

        bonusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                USSDcall("*222*266%23");
            }
        });

        mobileDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                USSDcall("*222*328%23");
            }
        });

        sendDonation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String telefono = phoneNumber.getText().toString().trim();
                if (telefono.equals("")) {
                    Toast.makeText(getContext(), "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                    return;
                }
                String Monto = donateMount.getText().toString().trim();
                if (Monto.equals("")) {
                    Toast.makeText(getContext(), "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                    return;
                }
                String Clave = donateKey.getText().toString().trim();
                if (Clave.equals("")) {
                    Toast.makeText(getContext(), "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                    return;
                }

                String ussdd = "*234*1*" + telefono + "*" + Clave + "*" + Monto + "%23";
                USSDcall(ussdd);
            }

        });

        rechargeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String recargartelefono = rechargeCode.getText().toString().trim();
                if (recargartelefono.equals("")) {
                    Toast.makeText(getContext(), "Escriba el código de la recarga", Toast.LENGTH_SHORT).show();
                    return;
                }
                String ussdd = "*662*" + recargartelefono + "%23";

                USSDcall(ussdd);

            }
        });

        adelantaSaldoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String adelantarsaldo = adelantaSaldoMount.getText().toString().trim();
                if (adelantarsaldo.equals("")) {
                    Toast.makeText(getContext(), "Escriba 25 o 50 CUP", Toast.LENGTH_SHORT).show();
                    return;
                }

                String ussdd = "*234*3*1*" + adelantarsaldo + "%23";

                USSDcall(ussdd);

            }
        });

        SMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext().startActivity(new Intent(getContext(), SmsActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        VOZ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext().startActivity(new Intent(getContext(), VozActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        PlanAmigos1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PlanAmigosDialog(context);
            }
        });

        Emergencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext().startActivity(new Intent(getContext(), EmergenciaActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });


        return view;

    }


    @Override
    public void onResume() {
        super.onResume();
        //loadPromo();
    }


    @SuppressLint("IntentReset")
    private void pickContact() {
        @SuppressLint("IntentReset") Intent intent = new Intent("android.intent.action.PICK", Uri.parse("content://contacts"));
        intent.setType("vnd.android.cursor.dir/phone_v2");
        getActivity().startActivityForResult(intent, 1);
    }

    public void USSDcall(String ussd) {

        Intent r = new Intent();
        r.setAction(Intent.ACTION_CALL);
        r.setData(Uri.parse("tel:" + ussd + ""));

        if (Build.VERSION.SDK_INT >= 23) {
            if (getContext().checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1000);

            } else {

                startActivity(r);

            }

        } else {

            startActivity(r);

        }

    }

    @Override
    public final void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        try{
            String texto = data.getStringExtra("scaneo");
            rechargeCode.setText(texto);

        }catch (Exception e ){

        }

    }

    public class PlanAmigosDialog {

        public PlanAmigosDialog(final Context context) {
            final Dialog plamAmigosDialog = new Dialog(getContext());

            plamAmigosDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            plamAmigosDialog.setCancelable(true);
            plamAmigosDialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(Color.TRANSPARENT));
            plamAmigosDialog.setContentView(R.layout.dialog_plan_amigos);

            activar = plamAmigosDialog.findViewById(R.id.activar);
            activar.setOnClickListener(v -> USSDcall("*133*4*1%23"));
            adicionar = plamAmigosDialog.findViewById(R.id.adicionar);
            adicionar.setOnClickListener(v -> USSDcall("*133*4*2%23"));
            consultar = plamAmigosDialog.findViewById(R.id.consultar);
            consultar.setOnClickListener(v -> USSDcall("*133*4*3%23"));

            plamAmigosDialog.show();
        }

    }

}





