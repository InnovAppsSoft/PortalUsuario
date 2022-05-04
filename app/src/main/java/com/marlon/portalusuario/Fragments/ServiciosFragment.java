package com.marlon.portalusuario.Fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.google.android.datatransport.runtime.logging.Logging;
import com.marlon.portalusuario.EmergenciaActivity;
import com.marlon.portalusuario.LlamadaOcultoActivity;
import com.marlon.portalusuario.Llamada_99Activity;
import com.marlon.portalusuario.PUNotifications.PUNotificationsActivity;
import com.marlon.portalusuario.PlanAmigos;
import com.marlon.portalusuario.R;
import com.marlon.portalusuario.SmsActivity;
import com.marlon.portalusuario.VozActivity;
import com.marlon.portalusuario.etecsa_scraping.Promo;
import com.marlon.portalusuario.etecsa_scraping.PromoSliderAdapter;
import com.marlon.portalusuario.codescanner.ScannerActivity;
import com.marlon.portalusuario.logging.JCLogging;
import com.marlon.portalusuario.senal.AppConfiguracionTool;
import com.marlon.portalusuario.util.SSLHelper;
import com.marlon.portalusuario.util.Util;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ServiciosFragment<b> extends Fragment {

    private static Context context;

    // PROMO ETECSA CAROUSEL
    private CardView cvCarousel;
    private static SliderView sliderView;
    private ProgressBar progressBar;
    private LinearLayout error;
    private TextView try_again;

    private LinearLayout Linear,Linear1;
    private FrameLayout notificationBtn;

    private SharedPreferences salva,nochesalva;
    private TextView cartBadge;
    private CardView SMS, VOZ, PlanAmigos1, Emergencia;
    private RelativeLayout creditBtn, bonusBtn, mobileDataBtn, callPrivateBtn, call99Btn, voicePlansBtn, friendsPlanBtn, smsPlansBtn;
    private EditText donateMount, donateKey, rechargeCode, adelantaSaldoMount;
    public static EditText phoneNumber;
    private ImageView sendDonation, adelantaSaldoBtn, rechargeBtn, contactSBtn, scanQRRechargeCode;

    private TelephonyManager j;
    private TextView networkClass,Fecha;
    private TelephonyManager telephonyManager;
    private TextView tvSignal;
    private com.marlon.Utils utils;
    private Util util;

    private JCLogging logging;

    static final int PICK_CONTACT_REQUEST = 1;

    public List<Promo> promoCache;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_servicios, container, false);
        // check first time opened
        isFirstTime();
        // check Cubacel operator
        checkOperator();
        // check if there are unseen notifications
        cartBadge = view.findViewById(R.id.cart_badge);
        setupBadge();
        notificationBtn = view.findViewById(R.id.notificationBtn);
        notificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), PUNotificationsActivity.class);
                startActivity(i);
            }
        });
        util = new Util();
        logging = new JCLogging(getActivity());
        // ui components init
        // etecsa carousel
        cvCarousel = view.findViewById(R.id.cvCarousel);
        sliderView = view.findViewById(R.id.imageSlider);
        progressBar = view.findViewById(R.id.progressBar);
        error = view.findViewById(R.id.error);
        try_again = view.findViewById(R.id.try_again);
        try_again.setPaintFlags(try_again.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        try_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPromo();
            }
        });
        loadPromo();
        //
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
                getContext().startActivity(new Intent(getContext(), PlanAmigos.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
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

    public void loadPromo(){
        // hiding promos card view
        cvCarousel.setVisibility(View.GONE);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean show_etecsa_promo_carousel = settings.getBoolean("show_etecsa_promo_carousel", true);
        logging.message("Loading settings::show_etecsa_promo_carousel=" + String.valueOf(show_etecsa_promo_carousel), null);
        if (show_etecsa_promo_carousel) {
            cvCarousel.setVisibility(View.VISIBLE);

            //
            logging.message("Starting etecsa's promo scraping", null);
            // mostrar progress && ocultar carrusel
            sliderView.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            // mostrar error
            error.setVisibility(View.INVISIBLE);
            //
            if (promoCache != null && !promoCache.isEmpty()){
                logging.message("Loading Etecsa's Promo from cache", null);
                updatePromoSlider(promoCache);
                return;
            }
            //
            if (Util.isConnected(getActivity())) {
                // llamada al metodo de scraping
                try{
                    new scrapingPromo().execute();
                }catch (Exception ex){
                    ex.printStackTrace();
                    logging.error(null, null, ex);
                }
            } else {
                logging.message("Internet connection unavailable", null);
                // ocultar progress && carrusel
                sliderView.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                // mostrar error
                error.setVisibility(View.VISIBLE);
            }
        }
    }

    // scrapear Promo de Etecsa
    public class scrapingPromo extends AsyncTask<Void, Void, List<Promo>> {
        private boolean success;
        // cuando se termine de ejecutar la accion doInBackground
        public void onPostExecute(List<Promo> promos) {
            super.onPostExecute(promos);
            // adapter para cada item
            if (success) {
                try{
                    // ocultar error
                    error.setVisibility(View.INVISIBLE);
                    // mostrar card view
                    sliderView.setVisibility(View.VISIBLE);
                    //
                    updatePromoSlider(promos);
                    // ocultar progress bar
                    progressBar.setVisibility(View.INVISIBLE);
                }catch (Exception ex){
                    ex.printStackTrace();
                    logging.error(null, null, ex);
                }
            }else{
                // ocultar progress bar
                progressBar.setVisibility(View.INVISIBLE);
                // ocultar card view
                sliderView.setVisibility(View.INVISIBLE);
                // mostrar error
                error.setVisibility(View.VISIBLE);
            }
        }
        public List<Promo> doInBackground(Void... voidArr) {
            List<Promo> promos = new ArrayList<Promo>();
            try {
                Connection.Response response = SSLHelper.getConnection("https://www.etecsa.cu").userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_6_8) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.122 Safari/534.30").timeout(30000).ignoreContentType(true).method(Connection.Method.GET).followRedirects(true).execute();
                if (response.statusCode() == 200) {
                    Document parsed = response.parse();
                    // CAROUSEL
                    Elements carousel = parsed.select("div.carousel-inner").select("div.carousel-item");
                    for (int i = 0; i < carousel.size(); i++) {
                        Element items = carousel.get(i);
                        Element mipromoContent = items.selectFirst("div.carousel-item");
                        String link = items.selectFirst("div.mipromocion-contenido").select("a").attr("href");
                        String toText = mipromoContent.toString();
                        int idx1 = toText.indexOf("<div style=\"background: url(\'");
                        int idx2 = toText.indexOf("\');");
                        String divStyle = toText.substring(idx1, idx2);
                        divStyle = divStyle.replace("<div style=\"background: url(\'", "");
                        //
                        Element imageSvg = items.selectFirst("div.mipromocion-contenido").selectFirst("img");
                        String svg = imageSvg.attr("src");
                        //
                        promos.add(new Promo(svg, divStyle, link));
                    }
                    success = true;
                }else{
                    success = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                logging.error(null, null, e);
            }
            return promos;
        }
    }

    public void updatePromoSlider(List<Promo> list){
        if (!list.isEmpty()) {
            PromoSliderAdapter adapter = new PromoSliderAdapter(getActivity(), (ArrayList<Promo>) list);
            sliderView.setSliderAdapter(adapter);
            // setting up el slider view
            sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
            sliderView.setSliderTransformAnimation(SliderAnimations.CUBEINSCALINGTRANSFORMATION);
            sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
            sliderView.setIndicatorSelectedColor(Color.WHITE);
            sliderView.setIndicatorUnselectedColor(Color.GRAY);
            sliderView.setScrollTimeInSec(4);
            sliderView.startAutoCycle();
        }else{
            cvCarousel.setVisibility(View.GONE);
        }
    }

    public class rejectTermsDialogListener implements DialogInterface.OnClickListener {
        rejectTermsDialogListener() {
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
            requireActivity().finish();
        }
    }

    public class acceptTermsDialogListener implements DialogInterface.OnClickListener {
        acceptTermsDialogListener() {
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            AppConfiguracionTool.setIsPrimeraEjecucion(getContext(), true);
            dialogInterface.dismiss();
        }
    }

    public class okButtonListener implements DialogInterface.OnClickListener {
        okButtonListener() {
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
        }
    }

    private void checkOperator() {
        boolean isCubacelShow = AppConfiguracionTool.getIsCubacelShow(getContext());
        try {
            if (!this.j.getNetworkOperatorName().equalsIgnoreCase("CUBACEL") && !isCubacelShow) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setPositiveButton(getResources().getText(R.string.aceptar), new okButtonListener());
                AlertDialog create = builder.create();
                create.setMessage(getResources().getText(R.string.main_iscubacel));
                create.setCancelable(false);
                create.show();
                AppConfiguracionTool.setIsCubacelShow(getContext(), true);
            }
        } catch (Exception ignored) {
        }
    }

    private void isFirstTime() {
        if (!AppConfiguracionTool.getIsPrimeraEjecucion(getContext())) {
            try {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                String appTerms = getResources().getString(R.string.terminos);
                builder.setTitle(R.string.main_isprimera_ejecucion_title).setMessage(appTerms).setPositiveButton(getResources().getText(R.string.main_isprimera_ejecucion_acepto), new acceptTermsDialogListener()).setNegativeButton(getResources().getText(R.string.main_isprimera_ejecucion_noacepto), new rejectTermsDialogListener());
                AlertDialog create = builder.create();
                create.setCancelable(false);
                create.show();
            }catch (Exception ex){
                ex.printStackTrace();
                Logging.e("", "", ex);
            }
        }
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

    private void setupBadge() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        int count = sharedPreferences.getInt("notifications_count", 0);
        Log.e("UNSEE NOTIFICATIONS", String.valueOf(count));
        if (count == 0) {
            if (cartBadge.getVisibility() != View.GONE) {
                cartBadge.setVisibility(View.GONE);
            }
        } else {
            if (cartBadge.getVisibility() != View.VISIBLE) {
                cartBadge.setVisibility(View.VISIBLE);
            }
        }
        cartBadge.setText(String.valueOf(count));
    }
}





