package com.marlon.portalusuario;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.marlon.portalusuario.carrusel.Banner;
import com.marlon.portalusuario.carrusel.SSLHelper;
import com.marlon.portalusuario.carrusel.SliderAdapter;
import com.marlon.portalusuario.carrusel.Util;
import com.marlon.portalusuario.codescanner.ScannerActivity;
import com.marlon.portalusuario.senal.AppConfiguracionTool;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

import soup.neumorphism.NeumorphImageView;
import trikita.log.Log;

import static android.content.Context.MODE_PRIVATE;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ServiciosFragment<b> extends Fragment {

    private static Context context;
    private static SliderView sliderView;
    private static List<Banner> promo_imgs;
    private LinearLayout Linear;

    static Fragment newInstance(Context ctx) {
        context = ctx;
        return new ServiciosFragment();
    }

    private SharedPreferences salva,nochesalva;

    private LinearLayout SMS, VOZ, PlanAmigos1, Emergencia,PlanDatos, Web;
    private RelativeLayout saldo, bono, megas, LlamarPrivado, Llamar99, Microfono, AmigosPlan,SmsPlan;
    private EditText m, w, rt, Adelanta;
    public static EditText t;
    private ImageView buttonenviar, Pedir,buttonenviar1, Contactos, Camara;

    private NeumorphImageView info;

    private TelephonyManager j;
    private TextView networkClass,Fecha;
    private TelephonyManager telephonyManager;
    private TextView tvSignal;
    private Utils Utils;

    TextView f239b;

    static final int PICK_CONTACT_REQUEST = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_servicios, container, false);
        g();
        f();
        int donateMainOpen = AppConfiguracionTool.getDonateMainOpen(context);
        if (donateMainOpen == 50 || donateMainOpen == 100 || donateMainOpen == 150) {
            e();
        }
        if (donateMainOpen != -1 && donateMainOpen <= 200) {
            AppConfiguracionTool.donateAddOneOpen(context);

        }

        salva = context.getSharedPreferences("salva", MODE_PRIVATE);
        nochesalva = context.getSharedPreferences("nochesalva", Context.MODE_PRIVATE);

        String ss = salva.getString("keysalva", "");

        if (ss.equals("")) {

            TapTargetView.showFor(requireActivity(),
                    TapTarget.forView(view.findViewById(R.id.enseñar), "Servicios", "En este apartado encontrarás Recargar Saldo, Transferir Saldo, Adelantar Saldo, Plan Amigos,Voz y SMS")
                            .outerCircleColor(R.color.colorPrimary)
                            .outerCircleAlpha(0.96f)
                            .targetCircleColor(R.color.white)
                            .titleTextSize(20)
                            .titleTextColor(R.color.white)
                            .descriptionTextSize(15)
                            .descriptionTextColor(R.color.white)
                            .textColor(R.color.white)
                            .textTypeface(Typeface.SANS_SERIF)
                            .dimColor(R.color.white)
                            .drawShadow(true)
                            .cancelable(true)
                            .tintTarget(true)
                            .transparentTarget(false)
                            .targetRadius(60),

                                                    new TapTargetView.Listener() {
                                                        @Override
                                                        public void onTargetClick(TapTargetView view3) {
                                                            super.onTargetClick(view3);
                                                            salva.edit().putString("keysalva", "yapase").apply();
                                                        }


                                                    });


                                        }


        // init de la lista de banners
        promo_imgs = new ArrayList<Banner>();
        // init
        CardView cvCarousel = view.findViewById(R.id.cvCarousel);
        Linear = view.findViewById(R.id.error);
        sliderView = view.findViewById(R.id.imageSlider);
        //
        if (Util.isConnected(getContext())) {
            cvCarousel.setVisibility(View.VISIBLE);
            sliderView.setVisibility(View.VISIBLE);
            Linear.setVisibility(View.INVISIBLE);

            // llamada al metodo de scraping
            startScraping();
        }else {
            sliderView.setVisibility(View.INVISIBLE);
            cvCarousel.setVisibility(View.VISIBLE);
            Linear.setVisibility(View.VISIBLE);

        }



        SMS = (LinearLayout) view.findViewById(R.id.sms);
        VOZ = (LinearLayout) view.findViewById(R.id.voz);
        PlanAmigos1 = (LinearLayout) view.findViewById(R.id.PlanAmigos1);
        Emergencia = (LinearLayout) view.findViewById(R.id.emergancia);

        t = view.findViewById(R.id.telefono);
        m = view.findViewById(R.id.Monto);
        w = view.findViewById(R.id.Clave);
        buttonenviar = view.findViewById(R.id.Enviar);
        rt = view.findViewById(R.id.recargartelefono);
        buttonenviar1 = view.findViewById(R.id.Enviar1);
        Adelanta = view.findViewById(R.id.adesaldo);
        Pedir = view.findViewById(R.id.adelantar);
        Camara = view.findViewById(R.id.camera);


        saldo = view.findViewById(R.id.saldo);
        bono = view.findViewById(R.id.bono);
        megas = view.findViewById(R.id.megas);
        Llamar99 = view.findViewById(R.id.llamar99);
        LlamarPrivado = view.findViewById(R.id.llamarPrivado);
        Contactos = view.findViewById(R.id.Contactos);
        Microfono = view.findViewById(R.id.microfono);
        AmigosPlan = view.findViewById(R.id.amigosplan);
        SmsPlan = view.findViewById(R.id.smsplan);


        AmigosPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                USSDcall("*222*264%23");
            }
        });

        SmsPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                USSDcall("*222*767%23");
            }
        });

        Microfono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                USSDcall("*222*869%23");
            }
        });

        Camara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              startActivityForResult(new Intent(context, ScannerActivity.class),6932);

            }
        });


        Contactos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selecionar_contacto();
            }
        });

        Llamar99.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, Llamada_99Activity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        LlamarPrivado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, LlamadaOcultoActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }

        });

        saldo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                USSDcall("*222%23");
            }
        });

        bono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                USSDcall("*222*266%23");
            }
        });

        megas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                USSDcall("*222*328%23");
            }
        });

        buttonenviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String telefono = t.getText().toString().trim();
                if (telefono.equals("")) {
                    Toast.makeText(context, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                    return;
                }
                String Monto = m.getText().toString().trim();
                if (Monto.equals("")) {
                    Toast.makeText(context, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                    return;
                }
                String Clave = w.getText().toString().trim();
                if (Clave.equals("")) {
                    Toast.makeText(context, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                    return;
                }

                String ussdd = "*234*1*" + telefono + "*" + Clave + "*" + Monto + "%23";
                USSDcall(ussdd);
            }

        });

        buttonenviar1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String recargartelefono = rt.getText().toString().trim();
                if (recargartelefono.equals("")) {
                    Toast.makeText(context, "Escriba el código de la recarga", Toast.LENGTH_SHORT).show();
                    return;
                }
                String ussdd = "*662*" + recargartelefono + "%23";

                USSDcall(ussdd);

            }
        });

        Pedir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String adelantarsaldo = Adelanta.getText().toString().trim();
                if (adelantarsaldo.equals("")) {
                    Toast.makeText(context, "Escriba 25 o 50 CUP", Toast.LENGTH_SHORT).show();
                    return;
                }

                String ussdd = "*234*3*1*" + adelantarsaldo + "%23";

                USSDcall(ussdd);

            }
        });

        SMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, Sms.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        VOZ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, Voz.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        PlanAmigos1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, PlanAmigos.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        Emergencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, EmergenciaActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });


        return view;

    }


    // metodo que invoca a la tarea asincrona
    public void startScraping() {
        new scrapingBanners().execute();
    }

    // metodo asincrono
    public class scrapingBanners extends AsyncTask<Void, Void, Void> {
        // cuando se termine de ejecutar la accion doInBackground
        public void onPostExecute(Void r3) {
            super.onPostExecute(r3);
            // adapter para cada item
            SliderAdapter adapter = new SliderAdapter(context, (ArrayList<Banner>) promo_imgs);
            sliderView.setSliderAdapter(adapter);
            // setting up el slider view
            sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
            sliderView.setSliderTransformAnimation(SliderAnimations.CUBEINSCALINGTRANSFORMATION);
            sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
            sliderView.setIndicatorSelectedColor(Color.WHITE);
            sliderView.setIndicatorUnselectedColor(Color.GRAY);
            sliderView.setScrollTimeInSec(4);
            sliderView.startAutoCycle();
        }

        public Void doInBackground(Void... voidArr) {
            try {
                Document document2a = SSLHelper.getConnection("https://www.etecsa.cu").userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_6_8) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.122 Safari/534.30").timeout(30000).ignoreContentType(true).method(Connection.Method.GET).followRedirects(true).execute().parse();
                Elements carousel = document2a.select("div.carousel-inner").select("a");
                for (int i = 0; i < carousel.size(); i++) {
                    Element a = carousel.get(i);
                    String title = a.attr("title");
                    String link = a.attr("href");
                    Elements img_element = a.select("img");
                    String img = img_element.attr("src");
                    promo_imgs.add(new Banner(title, img, link));
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            return null;
        }
    }

    public class a implements DialogInterface.OnClickListener {
        a() {
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            Intent intent = new Intent();
            new TransferirDialogo(context);
            intent.putExtra("donar", true);
            dialogInterface.dismiss();

        }
    }

    public class g implements DialogInterface.OnClickListener {
        g() {
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
            requireActivity().finish();
        }
    }

    public class h implements DialogInterface.OnClickListener {
        h() {
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            AppConfiguracionTool.setIsPrimeraEjecucion(context, true);
            dialogInterface.dismiss();
        }
    }

    public class i implements DialogInterface.OnClickListener {
        i(ServiciosFragment serviciosFragment) {
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
        }
    }

    public class j implements DialogInterface.OnClickListener {
        j(ServiciosFragment serviciosFragment) {
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
        }
    }

    private void e() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.main_dialog_donate_title)
                .setMessage(R.string.main_action_about_donar_text)
                .setPositiveButton(getResources().getText(R.string.main_action_about_donar_button), new a())
                .setNegativeButton(getResources().getText(R.string.cancelar), new j(this));
        AlertDialog create = builder.create();
        create.setCancelable(false);
        create.show();
    }

    private void f() {
        boolean isCubacelShow = AppConfiguracionTool.getIsCubacelShow(context);
        try {
            if (!this.j.getNetworkOperatorName().equalsIgnoreCase("CUBACEL") && !isCubacelShow) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setPositiveButton(getResources().getText(R.string.aceptar), new i(this));
                AlertDialog create = builder.create();
                create.setMessage(getResources().getText(R.string.main_iscubacel));
                create.setCancelable(false);
                create.show();
                AppConfiguracionTool.setIsCubacelShow(context, true);
            }
        } catch (Exception ignored) {
        }
    }

    private void g() {
        if (!AppConfiguracionTool.getIsPrimeraEjecucion(context)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.main_isprimera_ejecucion_title).setMessage(R.string.main_isprimera_ejecucion_terminosuso).setPositiveButton(getResources().getText(R.string.main_isprimera_ejecucion_acepto), new h()).setNegativeButton(getResources().getText(R.string.main_isprimera_ejecucion_noacepto), new g());
            AlertDialog create = builder.create();
            create.setCancelable(false);
            create.show();
        }
    }

    public class TransferirDialogo {

        public TransferirDialogo(final Context context

        ) {

            final Dialog dialogo = new Dialog(context);

            dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogo.setCancelable(true);
            dialogo.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(Color.TRANSPARENT));
            dialogo.setContentView(R.layout.alert_dialog);

            final EditText userInput = (EditText) dialogo.findViewById(R.id.MontoDonar);
            final EditText userInput2 = (EditText) dialogo.findViewById(R.id.ClaveS);


            ImageView aceptar = (ImageView) dialogo.findViewById(R.id.ac);

            aceptar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String MontoDonar = userInput.getText().toString().trim();
                    if (MontoDonar.equals("")) {
                        Toast.makeText(context, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String ClaveS = userInput2.getText().toString().trim();
                    if (ClaveS.equals("")) {
                        Toast.makeText(context, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String ussdd = "*234*1*" + "54871663" + "*" + ClaveS + "*" + MontoDonar + "%23";

                    Intent r = new Intent();
                    r.setAction(Intent.ACTION_CALL);
                    r.setData(Uri.parse("tel:" + ussdd + ""));

                    if (Build.VERSION.SDK_INT >= 23) {
                        if (context.checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED) {
                            ServiciosFragment.this.requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1000);


                        } else {

                            startActivity(r);

                        }

                    } else {

                        startActivity(r);

                    }

                    dialogo.dismiss();
                }
            });

            dialogo.show();
        }

    }

    @SuppressLint("IntentReset")
    private void selecionar_contacto() {
        @SuppressLint("IntentReset") Intent intent = new Intent("android.intent.action.PICK", Uri.parse("content://contacts"));
        intent.setType("vnd.android.cursor.dir/phone_v2");
        getActivity().startActivityForResult(intent, 1);
    }

    public void USSDcall(String ussd) {

        Intent r = new Intent();
        r.setAction(Intent.ACTION_CALL);
        r.setData(Uri.parse("tel:" + ussd + ""));

        if (Build.VERSION.SDK_INT >= 23) {
            if (context.checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED) {
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
            rt.setText(texto);

        }catch (Exception e ){

        }

    }
}





