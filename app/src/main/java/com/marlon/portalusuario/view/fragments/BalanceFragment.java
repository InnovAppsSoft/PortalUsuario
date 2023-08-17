package com.marlon.portalusuario.view.fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telecom.PhoneAccountHandle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.marlon.portalusuario.R;
import com.marlon.portalusuario.databinding.FragmentBalanceBinding;
import com.marlon.portalusuario.databinding.LayoutProgressDialogBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@TargetApi(26)
public class BalanceFragment extends Fragment {

    private FragmentBalanceBinding binding;
    private SharedPreferences.Editor editor;

    int totalDias = 30;
    public String sim = "1";

    private LayoutProgressDialogBinding dialogProgress;
    private AlertDialog dialog;
    SharedPreferences sp_sim, balance;
    private List<PhoneAccountHandle> phoneAccountHandleList;
    public static final String simSlotName[] = {
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

    String simslot;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBalanceBinding.inflate(inflater, container, false);

        // Mensaje para Android menor que 8
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            AlertDialog.Builder alertdialogo1 = new AlertDialog.Builder(requireActivity());
            alertdialogo1.setTitle("Advertencia");
            alertdialogo1.setMessage(R.string.advertencia);
            alertdialogo1.setPositiveButton("Ok",null);
            alertdialogo1.create().show();
        }

        // SharedPreference select sim
        sp_sim = PreferenceManager.getDefaultSharedPreferences(getContext());
        String simSlot = sp_sim.getString("sim_preference", "0");
        simslot = sp_sim.getString("sim_preference", "0");

        BalanceNotification receiver = new BalanceNotification();
        getActivity()
                .registerReceiver(
                        receiver,
                        new IntentFilter("android.intent.action.ACTION_PREFERENCE_CHANGED"));

        // update
        binding.fab.setOnClickListener(
                view -> {
                    dialogProgress =
                            LayoutProgressDialogBinding.inflate(LayoutInflater.from(getActivity()));
                    dialog =
                            new MaterialAlertDialogBuilder(getActivity())
                                    .setView(dialogProgress.getRoot())
                                    .setCancelable(false)
                                    .create();
                    dialog.show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        update_balances();
                    }
                });


        // ESCOGER SIM0-SIM1
        binding.checksimdual.setChecked(sim.equals("0"));
        binding.checksimdual.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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

        // guardar datos
        balance = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = balance.edit();
        binding.textSaldo.setText(balance.getString("saldo", "0.00 CUP"));
        binding.textVenceSaldo.setText(balance.getString("vence_saldo", "Expira: 00/00/00"));

        binding.textPaquete.setText(balance.getString("paquete", "0 MB"));
        binding.textPaqueteLte.setText(balance.getString("lte", "0 MB"));
        binding.textPaqueteNacional.setText(balance.getString("nacional", "0 MB"));
        binding.textPromociones.setText(balance.getString("promo", "Sin bonos promocionales"));

        binding.textVencePaquetes.setText(balance.getString("venceDat", " 0 días"));

        binding.textTarifa.setText(balance.getString("tarifa", "Tarifa no activa"));

        // minutos mensajes y vencimiento
        binding.textMinutos.setText(balance.getString("min", "00:00:00"));
        binding.textMensajes.setText(balance.getString("sms", "0 SMS"));
        binding.textVenceSMS.setText(balance.getString("vence_mensajes", "0 días"));

        // bolsa de mensajeria y diaria
        binding.textDiaria.setText(balance.getString("diaria", "0 MB"));
        binding.textVenceDiaria.setText(balance.getString("venceDiaria", "0 horas"));
        binding.textMensajeria.setText(balance.getString("mensajeria", "0 MB"));
        binding.textVenceMensajeria.setText(balance.getString("vence_mensajeria", "0 días"));

        // hora
        binding.textHora.setText(balance.getString("h", "Actualizado\n00:00"));
        // progress bar total dias en paquetes
        updateProgress();
        updateProgressMinSMS();
        updateProgressMensajeria();
        updateProgressDiaria();
        // Agregar un listener para detectar los cambios en "venceDat"
        SharedPreferences.OnSharedPreferenceChangeListener listener =
                new SharedPreferences.OnSharedPreferenceChangeListener() {
                    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                        if (key.equals("venceDat")) {
                            updateProgress();
                        }
                    }
                };
        balance.registerOnSharedPreferenceChangeListener(listener);

        return binding.getRoot();
    }

    public void updateProgress() {
        String day = balance.getString("venceDat", "0 días").replace(" días", "");
        if (isNumeric(day)) {
            int remainingDays = Integer.parseInt(day);
            Calendar calendar = Calendar.getInstance();
            Date currentDate = calendar.getTime();
            // calcular la fecha de caducidad
            calendar.add(Calendar.DAY_OF_MONTH, remainingDays);
            Date expirationDay = calendar.getTime();
            // calcular la cantidad de dias restantes
            long daysRemaining =
                    (expirationDay.getTime() - currentDate.getTime()) / (1000 * 60 * 60 * 24);
            // calcular el porcentaje
            int porcentage = (int) ((daysRemaining / (float) totalDias) * 100);
            binding.progressPaquete.setProgress(porcentage);
        } else {
            binding.progressPaquete.setProgress(100);
        }
    }

    public void updateProgressMinSMS() {
        String day = balance.getString("vence_mensajes", "0 días").replace(" días", "");
        if (isNumeric(day)) {
            int remainingDays = Integer.parseInt(day);
            Calendar calendar = Calendar.getInstance();
            Date currentDate = calendar.getTime();
            // calcular la fecha de caducidad
            calendar.add(Calendar.DAY_OF_MONTH, remainingDays);
            Date expirationDay = calendar.getTime();
            // calcular la cantidad de dias restantes
            long daysRemaining =
                    (expirationDay.getTime() - currentDate.getTime()) / (1000 * 60 * 60 * 24);
            // calcular el porcentaje
            int porcentage = (int) ((daysRemaining / (float) totalDias) * 100);
            binding.progressMinSms.setProgress(porcentage);
        } else {
            binding.progressMinSms.setProgress(100);
        }
    }

    public void updateProgressMensajeria() {
        String day = balance.getString("vence_mensajeria", "0 días").replace(" días", "");
        if (isNumeric(day)) {
            int remainingDays = Integer.parseInt(day);
            Calendar calendar = Calendar.getInstance();
            Date currentDate = calendar.getTime();
            // calcular la fecha de caducidad
            calendar.add(Calendar.DAY_OF_MONTH, remainingDays);
            Date expirationDay = calendar.getTime();
            // calcular la cantidad de dias restantes
            long daysRemaining =
                    (expirationDay.getTime() - currentDate.getTime()) / (1000 * 60 * 60 * 24);
            // calcular el porcentaje
            int porcentage = (int) ((daysRemaining / (float) totalDias) * 100);
            binding.progressMensajeria.setProgress(porcentage);
        } else {
            binding.progressMensajeria.setProgress(100);
        }
    }

    public void updateProgressDiaria() {
        String day = balance.getString("venceDiaria", "0 días").replace(" días", "");
        if (isNumeric(day)) {
            int remainingDays = Integer.parseInt(day);
            Calendar calendar = Calendar.getInstance();
            Date currentDate = calendar.getTime();
            // calcular la fecha de caducidad
            calendar.add(Calendar.DAY_OF_MONTH, remainingDays);
            Date expirationDay = calendar.getTime();
            // calcular la cantidad de dias restantes
            long daysRemaining =
                    (expirationDay.getTime() - currentDate.getTime()) / (1000 * 60 * 60 * 24);
            // calcular el porcentaje
            int porcentage = (int) ((daysRemaining / (float) totalDias) * 100);
            binding.progressDiaria.setProgress(porcentage);
        } else {
            binding.progressDiaria.setProgress(100);
        }
    }



    public boolean isNumeric(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        for (char c : text.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    public void updateHora() {
        Calendar calendar = Calendar.getInstance();
        Date dat = calendar.getTime();
        SimpleDateFormat datFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String hActual = datFormat.format(dat);
        editor.putString("h", "Actualizado\n" + hActual.toString());
        editor.apply();
        binding.textHora.setText("Actualizado\n" + hActual);
    }

    private void update_balances() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            TelephonyManager telephonyManager =
                    (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);

            USSDResponseCallback saldo =
                    new USSDResponseCallback() {
                        @Override
                        public void onUSSDReceived(String message) {
                            if (getActivity() != null && isVisible()) {
                                String saldo =
                                        message.toString()
                                                .replace("Saldo:", "")
                                                .replaceFirst("CUP(.*)", "")
                                                .trim();
                                StringBuilder sBuilder = new StringBuilder();
                                sBuilder.append(saldo);
                                sBuilder.append(" CUP");
                                editor.putString("saldo", sBuilder.toString().trim());
                                editor.apply();
                                binding.textSaldo.setText(sBuilder);

                                String vence_saldo =
                                        message.toString()
                                                .replaceFirst("(.*)vence ", "")
                                                .replace("-", "/")
                                                .replace(".", "")
                                                .trim();
                                StringBuilder vBuilder = new StringBuilder();
                                vBuilder.append("Expira: ");
                                vBuilder.append(vence_saldo);
                                editor.putString("vence_saldo", vBuilder.toString().trim());
                                editor.apply();
                                binding.textVenceSaldo.setText(vBuilder);
                            }
                        }

                        @Override
                        public void onUSSDReceivedFailed() {
                            if (getActivity() != null && isVisible()) {
                                Toast.makeText(
                                                getActivity(),
                                                "¡Error en la red! No fue posible consultar su saldo",
                                                Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                    };

            USSDResponseCallback datos =
                    new USSDResponseCallback() {
                        @Override
                        public void onUSSDReceived(String message) {
                            if (getActivity() != null && isVisible()) {
                                String tarifa =
                                        message.toString()
                                                .replaceFirst("Usted debe adquirir(.*)", "0 MB")
                                                .replaceFirst("Paquetes(.*)", "")
                                                .replaceFirst("Diaria(.*)", "")
                                                .replaceFirst("Mensajeria(.*)", "")
                                                .replaceFirst("(.*)Tarifa:", "")
                                                .replace(
                                                        "No activa.",
                                                        getString(R.string.tarifa_desactiva))
                                                .replace(
                                                        "Activa", getString(R.string.tarifa_activa))
                                                .replace(".", "");
                                editor.putString("tarifa", tarifa.toString().trim());
                                editor.apply();
                                binding.textTarifa.setText(tarifa);

                                // estado de paquete 3G/LTE
                                String s =
                                        message.toString()
                                                .replaceFirst("(.*)No activa.", "")
                                                .replaceFirst("(.*)Activa.", "");
                                String paquete =
                                        s.replaceFirst("Usted debe adquirir(.*)", "0 MB")
                                                .replaceFirst("(.*)Paquetes: ", "")
                                                .replaceFirst("\\+(.*)", "")
                                                .replaceFirst("Diaria(.*)", "0 MB")
                                                .replaceFirst("Mensajeria(.*)", "0 MB")
                                                .replaceFirst("validos(.*)", "")
                                                .replaceFirst("no activos(.*)", "");
                                editor.putString("paquete", paquete.toString().trim());
                                editor.apply();
                                binding.textPaquete.setText(paquete);

                                // estado de paquete LTE
                                String lte =
                                        s.replaceFirst("Usted debe adquirir(.*)", "0 MB")
                                                .replaceFirst("(.*)Paquetes: ", "")
                                                .replaceFirst("Diaria(.*)", "0 MB")
                                                .replaceFirst("Mensajeria(.*)", "0 MB")
                                                .replaceFirst("(.*)\\+", "")
                                                .replaceFirst("LTE(.*)", "")
                                                .replaceFirst("(.*)validos(.*)", "0 MB")
                                                .replaceFirst("no activos(.*)", "")
                                                .trim();
                                editor.putString("lte", lte.toString().trim());
                                editor.apply();
                                binding.textPaqueteLte.setText(lte);

                                // vence paquetes
                                String vence_datos =
                                        message.toString()
                                                .replaceFirst("(.*)validos", "")
                                                .replace("dias.", "días")
                                                .replace("no activos", "sin usar")
                                                .replaceFirst("(.*)LTE", "");
                                editor.putString("venceDat", vence_datos.toString().trim());
                                editor.apply();
                                binding.textVencePaquetes.setText(vence_datos);

                                // estado mensajeria
                                String mensajeria =
                                        s.replaceFirst("Usted debe adquirir(.*)", "0 MB")
                                                .replaceFirst("(.*)Mensajeria:", "")
                                                .replaceFirst("Diaria:(.*)", "0 MB")
                                                .replaceFirst("Paquetes(.*)", "0 MB")
                                                .replaceFirst("validos(.*)", "")
                                                .replace("no activos.", "");
                                editor.putString("mensajeria", mensajeria.toString().trim());
                                editor.apply();
                                binding.textMensajeria.setText(mensajeria);

                                // vence mensajeria
                                String vence_mensajeria =
                                        s.replaceFirst(
                                                        "Usted debe adquirir(.*)",
                                                        getString(R.string.vence_dias))
                                                .replaceFirst("(.*)Mensajeria:", "")
                                                .replaceFirst(
                                                        "Diaria:(.*)",
                                                        getString(R.string.vence_dias))
                                                .replaceFirst(
                                                        "Paquetes(.*)",
                                                        getString(R.string.vence_dias))
                                                .replaceFirst("(.*)validos", "")
                                                .replace("vencen hoy", "vence hoy")
                                                .replace("no activos.", "")
                                                .replace("dias.", getString(R.string.dias));
                                editor.putString(
                                        "vence_mensajeria", vence_mensajeria.toString().trim());
                                editor.apply();
                                binding.textVenceMensajeria.setText(vence_mensajeria);

                                // --> estado bolsa diaria
                                String diaria =
                                        s.replace("Paquetes: No dispone de MB.", "")
                                                .replaceFirst("Usted debe adquirir(.*)", "0 MB")
                                                .replaceFirst("Paquetes(.*)", "0 MB")
                                                .replace("Diaria:", "")
                                                .replaceFirst("Mensajeria(.*)", "0 MB")
                                                .replace("no activos.", "")
                                                .replaceFirst("validos(.*)", "");
                                editor.putString("diaria", diaria.toString().trim());
                                editor.apply();
                                binding.textDiaria.setText(diaria);

                                // --> Vence bolsa diaria
                                String vence_diaria =
                                        s.replaceFirst("Usted debe adquirir", "")
                                                .replace("Diaria:", "")
                                                .replaceFirst("Mensajeria(.*)", "")
                                                .replaceFirst(
                                                        "Paquetes(.*)",
                                                        getString(R.string.vence_horas))
                                                .replaceFirst("(.*)validos", "")
                                                .replaceFirst("(.*)no activos", "24 horas")
                                                .replace("horas.", getString(R.string.horas));
                                editor.putString("venceDiaria", vence_diaria.toString().trim());
                                editor.apply();
                                binding.textVenceDiaria.setText(vence_diaria);

                                // progress
                                updateProgress();
                            }
                        }

                        @Override
                        public void onUSSDReceivedFailed() {
                            if (getActivity() != null && isVisible()) {
                                Toast.makeText(
                                                getActivity(),
                                                "¡Error en la red! No fue posible consultar sus datos",
                                                Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                    };
            USSDResponseCallback bonos =
                    new USSDResponseCallback() {
                        @Override
                        public void onUSSDReceived(String message) {
                            if (getActivity() != null && isVisible()) {
                                String nacional =
                                        message.toString()
                                                .replaceFirst("(.*)Datos.cu ", "")
                                                .replaceFirst("Usted no dispone(.*)", "0 MB")
                                                .replace("no activos", "")
                                                .replaceFirst("vence(.*)", "");
                                editor.putString("nacional", nacional.toString().trim());
                                editor.apply();
                                binding.textPaqueteNacional.setText(nacional);

                                // promo
                                String promo =
                                        message.toString()
                                                .replaceFirst(
                                                        "Datos.cu(.*)", "Sin bonos promocionales");
                                editor.putString("promo", promo.toString().trim());
                                editor.apply();
                                binding.textPromociones.setText(promo);
                            }
                        }

                        @Override
                        public void onUSSDReceivedFailed() {
                            if (getActivity() != null && isVisible()) {
                                Toast.makeText(
                                                getActivity(),
                                                "¡Error en la red! No fue posible consultar sus bonos",
                                                Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                    };

            USSDResponseCallback mensajes =
                    new USSDResponseCallback() {
                        @Override
                        public void onUSSDReceived(String message) {
                            if (getActivity() != null && isVisible()) {
                                String mensajes =
                                        message.toString()
                                                .replaceFirst("(.*)dispone de", "")
                                                .replaceFirst("validos(.*)", "")
                                                .replaceFirst(
                                                        "Usted no dispone de SMS.(.*)", "0 SMS")
                                                .replaceFirst("Usted debe adquirir(.*)", "0 SMS")
                                                .replace("no activos.", "");
                                editor.putString("sms", mensajes.toString().trim());
                                editor.apply();
                                binding.textMensajes.setText(mensajes);

                                // vence mensajes y minutos
                                String vence_mensajes =
                                        message.toString()
                                                .replaceFirst(
                                                        "Usted debe adquirir(.*)",
                                                        getString(R.string.vence_dias))
                                                .replaceFirst("Usted no dispone de SMS.(.*)", "")
                                                .replaceFirst("Para una nueva(.*)", "")
                                                .replaceFirst("(.*)validos por", "")
                                                .replace("1 dia.", "24h")
                                                .replace("(.*)no activos.", "30 días")
                                                .replace("dias", getString(R.string.dias));
                                editor.putString(
                                        "vence_mensajes", vence_mensajes.toString().trim());
                                editor.apply();

                                binding.textVenceSMS.setText(vence_mensajes);
                            }
                        }

                        @Override
                        public void onUSSDReceivedFailed() {
                            if (getActivity() != null && isVisible()) {
                                Toast.makeText(
                                                getActivity(),
                                                "¡Error en la red! No fue posible consultar sus mensajes",
                                                Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                    };

            USSDResponseCallback minutos =
                    new USSDResponseCallback() {
                        @Override
                        public void onUSSDReceived(String message) {
                            if (getActivity() != null && isVisible()) {
                                String minutos =
                                        message.toString()
                                                .replaceFirst("(.*)dispone de", "")
                                                .replaceFirst("MIN(.*)", "")
                                                .replaceFirst("Usted debe adquirir(.*)", "00:00:00")
                                                .replace("no activos", "");
                                editor.putString("min", minutos.toString().trim());
                                editor.apply();
                                binding.textMinutos.setText(minutos);
                            }
                        }

                        @Override
                        public void onUSSDReceivedFailed() {
                            if (getActivity() != null && isVisible()) {
                                Toast.makeText(
                                                getActivity(),
                                                "¡Error en la red! No fue posible consultar sus minutos",
                                                Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                    };
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        getActivity(), new String[] {Manifest.permission.CALL_PHONE}, 20);
            } else {
                dialogProgress.textProgress.setText("Actualizando saldo...");
                telephonyManager.sendUssdRequest(
                        "*222#", saldo, new Handler(Looper.getMainLooper()));
            }

            new Handler(Looper.getMainLooper())
                    .postDelayed(
                            new Runnable() {
                                @Override
                                public void run() {
                                    if (ActivityCompat.checkSelfPermission(
                                            getContext(), Manifest.permission.CALL_PHONE)
                                            != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(
                                                getActivity(),
                                                new String[] {Manifest.permission.CALL_PHONE},
                                                20);
                                    } else {
                                        dialogProgress.textProgress.setText(
                                                "Actualizando datos...");
                                        telephonyManager.sendUssdRequest(
                                                "*222*328#",
                                                datos,
                                                new Handler(Looper.getMainLooper()));
                                    }
                                }
                            },
                            5000);
            new Handler(Looper.getMainLooper())
                    .postDelayed(
                            new Runnable() {
                                @Override
                                public void run() {
                                    if (ActivityCompat.checkSelfPermission(
                                            getContext(), Manifest.permission.CALL_PHONE)
                                            != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(
                                                getActivity(),
                                                new String[] {Manifest.permission.CALL_PHONE},
                                                20);
                                    } else {
                                        dialogProgress.textProgress.setText(
                                                "Actualizando bonos...");
                                        telephonyManager.sendUssdRequest(
                                                "*222*266#",
                                                bonos,
                                                new Handler(Looper.getMainLooper()));
                                    }
                                }
                            },
                            10000);

            new Handler(Looper.getMainLooper())
                    .postDelayed(
                            new Runnable() {
                                @Override
                                public void run() {
                                    if (ActivityCompat.checkSelfPermission(
                                            getContext(), Manifest.permission.CALL_PHONE)
                                            != PackageManager.PERMISSION_GRANTED) {

                                        ActivityCompat.requestPermissions(
                                                getActivity(),
                                                new String[] {Manifest.permission.CALL_PHONE},
                                                20);
                                    } else {
                                        dialogProgress.textProgress.setText(
                                                "Actualizando mensajes...");
                                        telephonyManager.sendUssdRequest(
                                                "*222*767#",
                                                mensajes,
                                                new Handler(Looper.getMainLooper()));
                                    }
                                }
                            },
                            15000);
            new Handler(Looper.getMainLooper())
                    .postDelayed(
                            new Runnable() {
                                @Override
                                public void run() {
                                    if (ActivityCompat.checkSelfPermission(
                                            getContext(), Manifest.permission.CALL_PHONE)
                                            != PackageManager.PERMISSION_GRANTED) {

                                        ActivityCompat.requestPermissions(
                                                getActivity(),
                                                new String[] {Manifest.permission.CALL_PHONE},
                                                20);
                                    } else {
                                        dialogProgress.textProgress.setText(
                                                "Actualizando minutos...");
                                        telephonyManager.sendUssdRequest(
                                                "*222*869#",
                                                minutos,
                                                new Handler(Looper.getMainLooper()));
                                    }
                                }
                            },
                            20000);

            // stop refresh
            new Handler(Looper.getMainLooper())
                    .postDelayed(
                            new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    updateNotification();
                                    updateHora();
                                }
                            },
                            25000);
        }
    }

    private void updateNotification() {
        SharedPreferences sp_notification =
                PreferenceManager.getDefaultSharedPreferences(getContext());
        if (sp_notification.getBoolean("notifi", false)) {
            getActivity().sendBroadcast(new Intent(getActivity(), BalanceNotification.class));
        }
    }

    public abstract class USSDResponseCallback extends TelephonyManager.UssdResponseCallback {

        @Override
        public void onReceiveUssdResponse(
                TelephonyManager telephonyManager, String request, CharSequence response) {
            super.onReceiveUssdResponse(telephonyManager, request, response);

            onUSSDReceived(response.toString());
        }

        @Override
        public void onReceiveUssdResponseFailed(
                TelephonyManager telephonyManager, String request, int failureCode) {
            super.onReceiveUssdResponseFailed(telephonyManager, request, failureCode);
            onUSSDReceivedFailed();
        }

        public abstract void onUSSDReceived(String message);

        public abstract void onUSSDReceivedFailed();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateProgress();
    }
}
