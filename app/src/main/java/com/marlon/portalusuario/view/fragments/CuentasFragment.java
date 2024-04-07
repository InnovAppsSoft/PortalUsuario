package com.marlon.portalusuario.view.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telecom.PhoneAccountHandle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.marlon.portalusuario.R;
import com.marlon.portalusuario.perfil.ImageSaver;
import com.marlon.portalusuario.perfil.PerfilActivity;
import com.marlon.portalusuario.widgets.WidgetUSSD;

import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class CuentasFragment extends Fragment {
    private SwipeRefreshLayout Refrescar;
    private TextView saldotext, expiratext, minutostext, mensajestext, venceminutossms, datostext, datoslte, datosnacionales, VenceDatosI, bolsasms,vencebolsasms,bolsadiaria,vencebolsadiaria,Actulizar;
    TelephonyManager manager, manager2, managerMain;
    private TextView TextoNombre,Saludo,Numero, Correo;
    SharedPreferences sp_cuentas;
    SharedPreferences.Editor editor;
    CheckBox escogerSim;

    // TODO: account handle and sim slot
    private List<PhoneAccountHandle> phoneAccountHandleList;
    public static final String[] simSlotName = {
            "extra_asus_dial_use_dualsim",
            "com.android.phone.extra.slot",
            "slot",
            "simslot",
            "sim_slot",
            "subscription",
            "Subscription",
            "phone",
            "com.android.phone.DialingMode",
            "simSlot",
            "slot_id",
            "simId",
            "simnum",
            "phone_type",
            "slotId",
            "slotIdx"
    };
    // TODO: preference dualSim
    public String sim ="0";
    public SharedPreferences sp_sim;

    AdView mAdView;
    private CircleImageView imgperfil;
    private ImageView editarimagen;
    private TextView promobonos;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_cuentas, container, false);
        // Mensaje para Android menor que 8
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            AlertDialog.Builder alertdialogo1 = new AlertDialog.Builder(requireActivity());
            alertdialogo1.setTitle("Advertencia");
            alertdialogo1.setMessage(R.string.advertencia);
            alertdialogo1.setPositiveButton("Ok",null);
            alertdialogo1.create().show();
        }
        // ui components init
        Refrescar = v.findViewById(R.id.swipeRefresh);
        saldotext = v.findViewById(R.id.text_cuentas_saldo);
        expiratext = v.findViewById(R.id.text_cuentas_vence_sim);
        minutostext = v.findViewById(R.id.text_cuentas_minutos);
        mensajestext = v.findViewById(R.id.text_cuentas_mensajes);
        venceminutossms = v.findViewById(R.id.text_cuentas_vence_min_sms);
        datostext = v.findViewById(R.id.text_cuentas_datos);
        datoslte = v.findViewById(R.id.text_cuentas_datos_lte);
        datosnacionales = v.findViewById(R.id.text_cuentas_datos_nacionales);
        VenceDatosI = v.findViewById(R.id.text_cuentas_vence_datos);
        bolsasms = v.findViewById(R.id.text_cuentas_mensajeria);
        vencebolsasms = v.findViewById(R.id.text_cuentas_vence_mensajeria);
        bolsadiaria = v.findViewById(R.id.text_cuentas_diaria);
        vencebolsadiaria = v.findViewById(R.id.text_cuentas_vence_diaria);
        Actulizar =v.findViewById(R.id.text_hora_update);
        TextoNombre = v.findViewById(R.id.textname);
        Saludo = v.findViewById(R.id.text_saludo_perfil);
        imgperfil = v.findViewById(R.id.img_drawer_perfil);
        editarimagen = v.findViewById(R.id.editar);
        Numero = v.findViewById(R.id.numerotext);
        Correo = v.findViewById(R.id.correotext);
        escogerSim = v.findViewById(R.id.check_sim_dual);
        promobonos = v.findViewById(R.id.button_cuentas_bono);

        // TODO: SharedPreferences para guardar datos de cuentas
        sp_cuentas = requireActivity().getSharedPreferences("cuentas", Context.MODE_PRIVATE);
        editor = sp_cuentas.edit();

        // TODO: Preferences DualSIM
        sp_sim = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sim = (sp_sim.getString(getString(R.string.sim_key), "0"));

        // ADS //
        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = v.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        AdView adView = new AdView(getContext());
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-9665109922019776/1173610479");
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdImpression() {
                // Code to be executed when an impression is recorded
                // for an ad.
            }

            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

        });

        // ESCOGER SIM0-SIM1
        escogerSim.setChecked(sim.equals("1"));
        escogerSim.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sim = "0";
                } else {
                    sim = "1";
                }
                sp_sim.edit().putString("sim_key", sim).apply();
                }

        });
        return v;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(View arg0, Bundle arg1) {
        super.onViewCreated(arg0, arg1);

        // TODO: Mostrar saludo con el nombre del usuario
        SharedPreferences sp_perfil = getActivity().getSharedPreferences("profile", Context.MODE_PRIVATE);
        String name = sp_perfil.getString("nombre", "");
        String numero = sp_perfil.getString("numero", "");
        String nauta = sp_perfil.getString("nauta", "");
        if (name.isEmpty()) {
            TextoNombre.setText("Usuario");
        } else {
            TextoNombre.setText(name);
        }
        if (numero.isEmpty()) {
            Numero.setText("Numero");
        } else {
            Numero.setText(numero);
        }
        if (nauta.isEmpty()) {
            Correo.setText("Nauta");
        } else {
            Correo.setText(nauta);
        }
        // saludo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime currentTime = LocalDateTime.now();
            if (currentTime.getHour() < 12) {
                Saludo.setText(getString(R.string.title_good_morning));
            } else if (currentTime.getHour() >= 12 && currentTime.getHour() < 18) {
                Saludo.setText(getString(R.string.title_good_afternoon));
            } else {
                Saludo.setText(getString(R.string.title_good_night));
            }
        } else {
            Saludo.setText("Hola");
        }
        editarimagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), PerfilActivity.class));

            }
        });

        // TODO: Perfil en el header
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Bitmap load =
                    new ImageSaver(getContext())
                            .setFileName("IMG.png")
                            .setDirectoryName("PortalUsuario")
                            .load();
            if (load == null) {
                imgperfil.setImageResource(R.drawable.portal);
            } else {
                imgperfil.setImageBitmap(load);
            }
        } else {
            Bitmap load =
                    new ImageSaver(getContext())
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

        // TODO: Preferences DualSIM
        sp_sim = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sim = (sp_sim.getString(getString(R.string.sim_key), "0"));


        // TODO: Ver bonos promocionales
       promobonos.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String promo = sp_cuentas.getString("bonos", null);
                        AlertDialog.Builder alertdialogo = new AlertDialog.Builder(requireActivity());
                        alertdialogo.setMessage(promo);
                        alertdialogo.setPositiveButton("Ok",null);
                        alertdialogo.create().show();
                    }
                });

        // TODO: SwipeRefresh
        Refrescar.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        // Progress dialog
                        ProgressDialog progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setMax(100);
                        progressDialog.setTitle("Actualizando USSD");
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        // UPDATE
                        new Handler(Looper.getMainLooper())
                                .postDelayed(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                if (sim.equals("0")) {
                                                    for (String s : simSlotName) {
                                                        consultaSaldo("*222#", 0);
                                                    }
                                                } else if (sim.equals("1")) {
                                                    for (String s : simSlotName) {
                                                        consultaSaldo("*222#", 1);
                                                    }
                                                }
                                                progressDialog.setTitle(
                                                        "Actualizando saldo");
                                                progressDialog.setProgress(20);

                                                //// MINUTOS
                                                new Handler(Looper.getMainLooper())
                                                        .postDelayed(
                                                                new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        if (sim.equals("0")) {
                                                                            for (String s :
                                                                                    simSlotName) {
                                                                                consultaMinutos(
                                                                                        "*222*869#",
                                                                                        0);
                                                                            }
                                                                        } else if (sim.equals(
                                                                                "1")) {
                                                                            for (String s :
                                                                                    simSlotName) {
                                                                                consultaMinutos(
                                                                                        "*222*869#",
                                                                                        1);
                                                                            }
                                                                        }
                                                                        progressDialog.setTitle(
                                                                                        "Actualizando minutos");
                                                                        progressDialog.setProgress(40);

                                                                        // MENSAJES
                                                                        new Handler(
                                                                                Looper
                                                                                        .getMainLooper())
                                                                                .postDelayed(
                                                                                        new Runnable() {
                                                                                            @Override
                                                                                            public
                                                                                            void
                                                                                            run() {
                                                                                                if (sim
                                                                                                        .equals(
                                                                                                                "0")) {
                                                                                                    for (String
                                                                                                            s :
                                                                                                            simSlotName) {
                                                                                                        consultaMensajes(
                                                                                                                "*222*767#",
                                                                                                                0);
                                                                                                    }
                                                                                                } else if (sim
                                                                                                        .equals(
                                                                                                                "1")) {
                                                                                                    for (String
                                                                                                            s :
                                                                                                            simSlotName) {
                                                                                                        consultaMensajes(
                                                                                                                "*222*767#",
                                                                                                                1);
                                                                                                    }
                                                                                                }
                                                                                                progressDialog.setTitle(
                                                                                                                "Actualizando mensajes");
                                                                                                progressDialog.setProgress(60);
                                                                                                // BONOS
                                                                                                new Handler(
                                                                                                        Looper
                                                                                                                .getMainLooper())
                                                                                                        .postDelayed(
                                                                                                                new Runnable() {
                                                                                                                    @Override
                                                                                                                    public
                                                                                                                    void
                                                                                                                    run() {
                                                                                                                        if (sim
                                                                                                                                .equals(
                                                                                                                                        "0")) {
                                                                                                                            for (String
                                                                                                                                    s :
                                                                                                                                    simSlotName) {
                                                                                                                                consultaBonos(
                                                                                                                                        "*222*266#",
                                                                                                                                        0);
                                                                                                                            }
                                                                                                                        } else if (sim
                                                                                                                                .equals(
                                                                                                                                        "1")) {
                                                                                                                            for (String
                                                                                                                                    s :
                                                                                                                                    simSlotName) {
                                                                                                                                consultaBonos(
                                                                                                                                        "*222*266#",
                                                                                                                                        1);
                                                                                                                            }
                                                                                                                        }
                                                                                                                        progressDialog.setTitle(
                                                                                                                                        "Actualizando bonos");
                                                                                                                        progressDialog.setProgress(80);

                                                                                                                        /// DATOS
                                                                                                                        new Handler(
                                                                                                                                Looper
                                                                                                                                        .getMainLooper())
                                                                                                                                .postDelayed(
                                                                                                                                        new Runnable() {
                                                                                                                                            @Override
                                                                                                                                            public
                                                                                                                                            void
                                                                                                                                            run() {
                                                                                                                                                if (sim
                                                                                                                                                        .equals(
                                                                                                                                                                "0")) {
                                                                                                                                                    for (String
                                                                                                                                                            s :
                                                                                                                                                            simSlotName) {
                                                                                                                                                        consultaDatos(
                                                                                                                                                                "*222*328#",
                                                                                                                                                                0);
                                                                                                                                                    }
                                                                                                                                                } else if (sim
                                                                                                                                                        .equals(
                                                                                                                                                                "1")) {
                                                                                                                                                    for (String
                                                                                                                                                            s :
                                                                                                                                                            simSlotName) {
                                                                                                                                                        consultaDatos(
                                                                                                                                                                "*222*328#",
                                                                                                                                                                1);
                                                                                                                                                    }
                                                                                                                                                }
                                                                                                                                                progressDialog.setTitle(
                                                                                                                                                                "Actualizando datos");
                                                                                                                                                progressDialog.setProgress(100);
                                                                                                                                                        Refrescar
                                                                                                                                                        .setRefreshing(
                                                                                                                                                                false);
                                                                                                                                                // hora
                                                                                                                                                SimpleDateFormat
                                                                                                                                                        hora =
                                                                                                                                                        new SimpleDateFormat(
                                                                                                                                                                "h:mm a");
                                                                                                                                                String
                                                                                                                                                        time =
                                                                                                                                                        hora
                                                                                                                                                                .format(
                                                                                                                                                                        new Date());
                                                                                                                                                editor
                                                                                                                                                        .putString(
                                                                                                                                                                "hora",
                                                                                                                                                                time);
                                                                                                                                                editor
                                                                                                                                                        .commit();
                                                                                                                                                Actulizar
                                                                                                                                                        .setText(
                                                                                                                                                                "Actualizado: "
                                                                                                                                                                        + time);
                                                                                                                                                new Handler(
                                                                                                                                                        Looper
                                                                                                                                                                .getMainLooper())
                                                                                                                                                        .postDelayed(
                                                                                                                                                                new Runnable() {
                                                                                                                                                                    @Override
                                                                                                                                                                    public
                                                                                                                                                                    void
                                                                                                                                                                    run() {
                                                                                                                                                                        progressDialog.dismiss();

                                                                                                                                                                    }
                                                                                                                                                                },
                                                                                                                                                                8000);
                                                                                                                                            }
                                                                                                                                        },
                                                                                                                                        6000);
                                                                                                                    }
                                                                                                                },
                                                                                                                6000);
                                                                                            }
                                                                                        },
                                                                                        5000);
                                                                    }
                                                                },
                                                                4000);
                                            }
                                        },
                                        1000);
                    }

                });

    }


    public void consultaSaldo(String ussdCode, int sim) {
        if (ussdCode.equalsIgnoreCase("")) return;
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.CALL_PHONE}, 234);
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
            manager2 = manager.createForSubscriptionId(2);
            managerMain = (sim == 0) ? manager : manager2;
            managerMain.sendUssdRequest(
                    ussdCode,
                    new TelephonyManager.UssdResponseCallback() {
                        @Override
                        public void onReceiveUssdResponse(
                                TelephonyManager telephonyManager,
                                String request,
                                CharSequence response) {
                            super.onReceiveUssdResponse(telephonyManager, request, response);

                            String message_saldo =
                                    response.toString()
                                            .replace("Saldo:", "")
                                            .replaceFirst("CUP(.*)", "")
                                            .trim();
                            StringBuilder sb1 = new StringBuilder();
                            sb1.append(message_saldo);
                            sb1.append(" CUP");
                            saldotext.setText(sb1);
                            editor.putString("saldo", sb1.toString());
                            editor.commit();

                            // TODO: String mostrar fecha de vencimiento de saldo principal
                            String message_vence =
                                    response.toString()
                                            .replaceFirst("(.*)vence", "")
                                            .replace("-", "/")
                                            .replace(".", "")
                                            .trim();
                            StringBuilder sb2 = new StringBuilder();
                            sb2.append("");
                            sb2.append(message_vence);
                            expiratext.setText(sb2);
                            editor.putString("vence_saldo", sb2.toString());
                            editor.commit();
                        }

                        @Override
                        public void onReceiveUssdResponseFailed(
                                TelephonyManager telephonyManager,
                                String request,
                                int failureCode) {
                            super.onReceiveUssdResponseFailed(
                                    telephonyManager, request, failureCode);

                            Log.e(
                                    "TAG",
                                    "onReceiveUssdResponseFailed: " + "" + failureCode + request);
                        }
                    },
                    new Handler(Looper.getMainLooper()));
        }
    }

    public void consultaMinutos(String ussdCode, int sim) {
        if (ussdCode.equalsIgnoreCase("")) return;
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.CALL_PHONE}, 234);
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
            manager2 = manager.createForSubscriptionId(2);
            managerMain = (sim == 0) ? manager : manager2;
            managerMain.sendUssdRequest(
                    ussdCode,
                    new TelephonyManager.UssdResponseCallback() {
                        @Override
                        public void onReceiveUssdResponse(
                                TelephonyManager telephonyManager,
                                String request,
                                CharSequence response) {
                            super.onReceiveUssdResponse(telephonyManager, request, response);
                            String minutos =
                                    response.toString()
                                            .replace(
                                                    "Usted debe adquirir un plan de minutos. Para una nueva compra marque *133#",
                                                    "00.00.00")
                                            .replace("Usted dispone de", "")
                                            .replaceFirst("MIN(.*)", "")
                                            .trim();
                            minutostext.setText(minutos);
                            editor.putString("minutos", minutos);
                            editor.commit();
                        }

                        @Override
                        public void onReceiveUssdResponseFailed(
                                TelephonyManager telephonyManager,
                                String request,
                                int failureCode) {
                            super.onReceiveUssdResponseFailed(
                                    telephonyManager, request, failureCode);

                            Log.e(
                                    "TAG",
                                    "onReceiveUssdResponseFailed: " + "" + failureCode + request);
                        }
                    },
                    new Handler(Looper.getMainLooper()));
        }
    }

    public void consultaMensajes(String ussdCode, int sim) {
        if (ussdCode.equalsIgnoreCase("")) return;
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.CALL_PHONE}, 234);
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
            manager2 = manager.createForSubscriptionId(2);
            managerMain = (sim == 0) ? manager : manager2;
            managerMain.sendUssdRequest(
                    ussdCode,
                    new TelephonyManager.UssdResponseCallback() {
                        @Override
                        public void onReceiveUssdResponse(
                                TelephonyManager telephonyManager,
                                String request,
                                CharSequence response) {
                            super.onReceiveUssdResponse(telephonyManager, request, response);
                            // cantidad de mensajes
                            String message_sms =
                                    response.toString()
                                            .replace(
                                                    "Usted debe adquirir un plan de SMS. Para una nueva compra marque *133#",
                                                    "0")
                                            .replace(
                                                    "Usted no dispone de SMS. Para una nueva compra marque *133#",
                                                    "0")
                                            .replace("Usted dispone de", "")
                                            .replace("no activos.", "")
                                            .replaceFirst("SMS(.*)", "")
                                            .trim();
                            mensajestext.setText(message_sms);
                            editor.putString("sms", message_sms);
                            editor.commit();

                            // Fecha vencimiento sms y voz
                            String message_vence_sms =
                                    response.toString()
                                            .replace(
                                                    "Usted debe adquirir un plan de SMS. Para una nueva compra marque *133#",
                                                    "0 días")
                                            .replace(
                                                    "Usted no dispone de SMS. Para una nueva compra marque *133#",
                                                    "0 días")
                                            .replaceFirst("(.*)por", "")
                                            .replace("dias", "días")
                                            .trim();
                            editor.putString(
                                    "vence_sms",
                                    message_vence_sms
                                            .replaceFirst("(.*)no activos.", "0 días"));
                            editor.commit();
                            venceminutossms.setText(message_vence_sms);

                            // no usp
                            String vence_sms_min =
                                    sp_cuentas.getString("vence_sms", message_vence_sms);
                        }

                        @Override
                        public void onReceiveUssdResponseFailed(
                                TelephonyManager telephonyManager,
                                String request,
                                int failureCode) {
                            super.onReceiveUssdResponseFailed(
                                    telephonyManager, request, failureCode);

                            Log.e(
                                    "TAG",
                                    "onReceiveUssdResponseFailed: " + "" + failureCode + request);
                        }
                    },
                    new Handler(Looper.getMainLooper()));
        }
    }

    public void consultaDatos(String ussdCode, int sim) {
        if (ussdCode.equalsIgnoreCase("")) return;
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.CALL_PHONE}, 234);
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager = (TelephonyManager) requireActivity().getSystemService(Context.TELEPHONY_SERVICE);
            manager2 = manager.createForSubscriptionId(2);
            managerMain = (sim == 0) ? manager : manager2;
            managerMain.sendUssdRequest(
                    ussdCode,
                    new TelephonyManager.UssdResponseCallback() {
                        @Override
                        public void onReceiveUssdResponse(
                                TelephonyManager telephonyManager,
                                String request,
                                CharSequence response) {
                            super.onReceiveUssdResponse(telephonyManager, request, response);
                            String todos_los_paquetes =
                                    "Tarifa: No activa. Diaria: 200 MB no activos. Mensajeria: 599.53 MB validos 8 dias. Paquetes: 3.57 GB + 3.10 GB LTE no activos.";

                            // bolsa diaria
                            String diaria =
                                    response.toString()
                                            .replace(
                                                    "Tarifa: No activa. Ud debe adquirir una oferta. Para una nueva compra marque *133#",
                                                    "0 MB")
                                            .replace(
                                                    "Tarifa: No activa. Paquetes: No dispone de MB.",
                                                    "0 MB")
                                            .replace("Tarifa: No activa. Diaria:", "")
                                            .replace(
                                                    "Tarifa: No activa. Diaria: No dispone de MB.",
                                                    "0 MB")
                                            .replace("Tarifa: No activa.", "0 MB")
                                            .replaceFirst("Mensajeria:(.*)", "")
                                            .replaceFirst("Paquetes:(.*)", "")
                                            .trim();
                            editor.putString(
                                    "diaria",
                                    diaria
                                            .replace("no activos.", "")
                                            .replaceFirst("validos(.*)", "")
                                            .replace("No dispone de MB.", "0 MB"));
                            editor.commit();
                            String bolsa_diaria = sp_cuentas.getString("diaria", diaria);
                            bolsadiaria.setText(bolsa_diaria);

                            // vencimiento de bolsa diaria
                            String message_vence_diaria =
                                    response.toString()
                                            .replace(
                                                    "Tarifa: No activa. Ud debe adquirir una oferta. Para una nueva compra marque *133#",
                                                    "Debe adquirir un paquete")
                                            .replace(
                                                    "Tarifa: No activa. Paquetes: No dispone de MB.",
                                                    "0 horas")
                                            .replace("Tarifa: No activa. Diaria:", "")
                                            .replace("Tarifa: No activa.", "0 horas")
                                            .replaceFirst("Mensajeria:(.*)", "")
                                            .replaceFirst("Paquetes:(.*)", "")
                                            .trim();
                            editor.putString(
                                    "vence_diaria",
                                    message_vence_diaria
                                            .replaceFirst("(.*)no activos.", "sin consumir")
                                            .replaceFirst("(.*)validos", "")
                                            .replace("No dispone de MB.", "0 horas")
                                            .replace("horas.", "horas"));
                            editor.commit();
                            String vence_diaria =
                                    sp_cuentas.getString(
                                            "vence_diaria", message_vence_diaria);
                            vencebolsadiaria.setText(vence_diaria);

                            // bolsa de mensajeria
                            String message_mensajeria =
                                    response.toString()
                                            .replace(
                                                    "Tarifa: No activa. Ud debe adquirir una oferta. Para una nueva compra marque *133#",
                                                    "0 MB")
                                            .replace(
                                                    "Tarifa: No activa. Paquetes: No dispone de MB.",
                                                    "0 MB")
                                            .replace(
                                                    "Tarifa: No activa. Diaria: No dispone de MB. Mensajeria:",
                                                    "")
                                            .replaceFirst("(.*)no activos. Mensajeria:", "")
                                            .replaceFirst("(.*)horas. Mensajeria:", "")
                                            .replace(
                                                    "Tarifa: No activa. Mensajeria: No dispone de MB.",
                                                    "0 MB")
                                            .replace("Tarifa: No activa. Mensajeria:", "")
                                            .replace("Tarifa: No activa.", "0 MB")
                                            .replaceFirst("Diaria:(.*)", "")
                                            .replaceFirst("Paquetes:(.*)", "")
                                            .trim();

                            editor.putString(
                                    "mensajeria",
                                    message_mensajeria
                                            .replace("vencen hoy.", "")
                                            .replaceFirst("validos(.*)", ""));
                            editor.commit();
                            String bolsa_msg =
                                    sp_cuentas.getString(
                                            "mensajeria", message_mensajeria);
                            bolsasms.setText(bolsa_msg);

                            // vence mensajeria
                            String message_vence_mensajeria =
                                    response.toString()
                                            .replace(
                                                    "Tarifa: No activa. Ud debe adquirir una oferta. Para una nueva compra marque *133#",
                                                    "0 días")
                                            .replace(
                                                    "Tarifa: No activa. Paquetes: No dispone de MB.",
                                                    "0 días")
                                            .replace(
                                                    "Tarifa: No activa. Diaria: No dispone de MB. Mensajeria:",
                                                    "")
                                            .replaceFirst("(.*)no activos. Mensajeria:", "")
                                            .replaceFirst("(.*)horas. Mensajeria:", "")
                                            .replace(
                                                    "Tarifa: No activa. Mensajeria: No dispone de MB.",
                                                    "0 días")
                                            .replace("Tarifa: No activa. Mensajeria:", "")
                                            .replace("Tarifa: No activa.", "0 días")
                                            .replaceFirst("Diaria:(.*)", "")
                                            .replaceFirst("Paquetes:(.*)", "")
                                            .trim();
                            editor.putString(
                                    "vence_mensajeria",
                                    message_vence_mensajeria
                                            .replaceFirst("(.*)validos", "")
                                            .replaceFirst("(.*)vencen hoy.", "vence hoy")
                                            .replace("dias.", "días"));
                            editor.commit();
                            String vence_mensajeria =
                                    sp_cuentas.getString("vence_mensajeria", "0 días");
                            vencebolsasms.setText(vence_mensajeria);

                            // paquetes
                            String message_paquete =
                                    response.toString()
                                            .replace(
                                                    "Tarifa: No activa. Ud debe adquirir una oferta. Para una nueva compra marque *133#",
                                                    "0 MB")
                                            .replace(
                                                    "Tarifa: No activa. Diaria: No dispone de MB.",
                                                    "0 MB")
                                            .replaceFirst("(.*)Paquetes:", "")
                                            .replaceFirst("\\+(.*)", "")
                                            .replaceFirst("Tarifa: No activa. Diaria:(.*)", "0 MB")
                                            .replaceFirst(
                                                    "Tarifa: No activa. Mensajeria:(.*)", "0 MB")
                                            .trim();
                            editor.putString(
                                    "paquete",
                                    message_paquete
                                            .replaceFirst("(.*)LTE(.*)", "0 MB")
                                            .replaceFirst("validos(.*)", ""));
                            editor.commit();
                            String paquete =
                                    sp_cuentas.getString("paquete", message_paquete);
                            datostext.setText(paquete);

                            // TODO: message paquete LTE
                            String message_lte =
                                    response.toString()
                                            .replace(
                                                    "Tarifa: No activa. Ud debe adquirir una oferta. Para una nueva compra marque *133#",
                                                    "0 MB")
                                            .replace(
                                                    "Tarifa: No activa. Diaria: No dispone de MB.",
                                                    "0 MB")
                                            .replaceFirst("(.*)Paquetes:", "")
                                            .replaceFirst("(.*)\\+", "")
                                            .replaceFirst("Tarifa: No activa. Diaria:(.*)", "0 MB")
                                            .replaceFirst(
                                                    "Tarifa: No activa. Mensajeria:(.*)", "0 MB")
                                            .trim();
                            String lte =
                                    message_lte
                                            .replaceFirst("LTE(.*)", "")
                                            .replaceFirst("(.*)validos(.*)", "0 MB")
                                            .replace("no activos.", "");
                            editor.putString("lte", lte);
                            editor.commit();
                            datoslte.setText(lte);

                            // TODO: vencimiento de datos
                            String vencimiento_datos =
                                    response.toString()
                                            .replace(
                                                    "Tarifa: No activa. Ud debe adquirir una oferta. Para una nueva compra marque *133#",
                                                    "0 días")
                                            .replace(
                                                    "Tarifa: No activa. Diaria: No dispone de MB.",
                                                    "0 MB")
                                            .replaceFirst("(.*)Paquetes:", "")
                                            .replaceFirst("Tarifa: No activa. Diaria:(.*)", "0 MB")
                                            .replaceFirst(
                                                    "Tarifa: No activa. Mensajeria:(.*)", "0 MB")
                                            .trim();
                            String vence =
                                    vencimiento_datos
                                            .replaceFirst("(.*)validos", "")
                                            .replace("dias.", "días")
                                            .replace("no activos", "0 días")
                                            .replaceFirst("(.*)LTE", "");
                            editor.putString("vence_datos", vence);
                            editor.commit();
                            VenceDatosI.setText(vence);
                        }

                        @Override
                        public void onReceiveUssdResponseFailed(
                                TelephonyManager telephonyManager,
                                String request,
                                int failureCode) {
                            super.onReceiveUssdResponseFailed(
                                    telephonyManager, request, failureCode);

                            Log.e(
                                    "TAG",
                                    "onReceiveUssdResponseFailed: " + "" + failureCode + request);
                        }
                    },
                    new Handler(Looper.getMainLooper()));

        }
    }

    public void consultaBonos(String ussdCode, int sim) {
        if (ussdCode.equalsIgnoreCase("")) return;
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(requireActivity()), Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.CALL_PHONE}, 234);
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager = (TelephonyManager) requireActivity().getSystemService(Context.TELEPHONY_SERVICE);
            manager2 = manager.createForSubscriptionId(2);
            managerMain = (sim == 0) ? manager : manager2;
            managerMain.sendUssdRequest(
                    ussdCode,
                    new TelephonyManager.UssdResponseCallback() {
                        @Override
                        public void onReceiveUssdResponse(
                                TelephonyManager telephonyManager,
                                String request,
                                CharSequence response) {
                            super.onReceiveUssdResponse(telephonyManager, request, response);
                            String datos_nacionales =
                                    response.toString()
                                            .replace("Usted no dispone de bonos activos.", "0 MB")
                                            .replaceFirst("(.*)Datos.cu ", "")
                                            .replaceFirst("vence(.*)", "")
                                            .trim();
                            editor.putString("datos_nacionales", datos_nacionales);
                            editor.commit();
                            datosnacionales.setText(datos_nacionales);

                            // TODO: Bonos en promoción
                            String bonos =
                                    response.toString()
                                            .replace("Usted no dispone de bonos activos.", "")
                                            .replaceFirst("Datos.cu(.*)", "")
                                            .trim();
                            String string_bono = bonos;
                            if (!TextUtils.isEmpty(string_bono)) {
                                promobonos.setVisibility(View.VISIBLE);
                            } else {
                                promobonos.setVisibility(View.GONE);
                            }
                            editor.putString("bonos", string_bono);
                            editor.commit();
                        }

                        @Override
                        public void onReceiveUssdResponseFailed(
                                TelephonyManager telephonyManager,
                                String request,
                                int failureCode) {
                            super.onReceiveUssdResponseFailed(
                                    telephonyManager, request, failureCode);

                            Log.e(
                                    "TAG",
                                    "onReceiveUssdResponseFailed: " + "" + failureCode + request);
                        }
                    },
                    new Handler(Looper.getMainLooper()));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sp_perfil = requireActivity().getSharedPreferences("profile", Context.MODE_PRIVATE);
        String name = sp_perfil.getString("nombre", "");
        String numero = sp_perfil.getString("numero", "");
        String nauta = sp_perfil.getString("nauta", "");
        if (name.isEmpty()) {
            TextoNombre.setText("Usuario");
        } else {
            TextoNombre.setText(name);
        }
        if (numero.isEmpty()) {
            Numero.setText("Numero");
        } else {
            Numero.setText(numero);
        }
        if (nauta.isEmpty()) {
            Correo.setText("Nauta");
        } else {
            Correo.setText(nauta);
        }
        // saldo movil
        String saldo = sp_cuentas.getString("saldo", "0.00 CUP");
        saldotext.setText(saldo);
        // vence saldo movil
        String vence_saldo = sp_cuentas.getString("vence_saldo", "00/00/00");
        expiratext.setText(vence_saldo);
        // minutos
        String minutos = sp_cuentas.getString("minutos", "00:00:00");
        minutostext.setText(minutos);
        // mensajes
        String mensajes = sp_cuentas.getString("sms", "0");
        mensajestext.setText(mensajes);
        // vence sms y voz
        String vence_min_sms = sp_cuentas.getString("vence_sms", "0 días");
        venceminutossms.setText(vence_min_sms);
        // bonos en promo
        String promo = sp_cuentas.getString("bonos", null);
        if (!TextUtils.isEmpty(promo)) {
            promobonos.setVisibility(View.VISIBLE);
        } else {
            promobonos.setVisibility(View.GONE);
        }
        // datos nacionales
        String nacionales = sp_cuentas.getString("datos_nacionales", "0 MB");
        datosnacionales.setText(nacionales);
        // paquetes
        String datos = sp_cuentas.getString("paquete", "0 MB");
        datostext.setText(datos);
        // lte
        String lte = sp_cuentas.getString("lte", "0 MB");
        datoslte.setText(lte);
        // vence datos
        String vence_datos = sp_cuentas.getString("vence_datos", "0 días");
        VenceDatosI.setText(vence_datos);
        // mensajeria y vencimiento
        String mensajeria = sp_cuentas.getString("mensajeria", "0 MB");
        String vence_mensajeria = sp_cuentas.getString("vence_mensajeria", "0 días");
        bolsasms.setText(mensajeria);
        vencebolsasms.setText(vence_mensajeria);
        // diaria y vencimiento
        String diaria = sp_cuentas.getString("diaria", "0 MB");
        String vence_diaria = sp_cuentas.getString("vence_diaria", "0 horas");
        bolsadiaria.setText(diaria);
        vencebolsadiaria.setText(vence_diaria);
        // update hora
        String time = sp_cuentas.getString("hora", "00:00");
        Actulizar.setText("Actualizado: " + time);

    }

}