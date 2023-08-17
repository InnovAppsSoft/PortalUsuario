package com.marlon.portalusuario.view.fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.marlon.portalusuario.R;
import com.marlon.portalusuario.activities.EmergenciaActivity;
import com.marlon.portalusuario.activities.LlamadaOcultoActivity;
import com.marlon.portalusuario.activities.Llamada_99Activity;
import com.marlon.portalusuario.activities.SmsActivity;
import com.marlon.portalusuario.activities.VozActivity;
import com.marlon.portalusuario.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private CardView activar,adicionar,consultar;

    private FragmentHomeBinding binding;
    SharedPreferences sp_sim;
    String simslot;

    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        setHasOptionsMenu(true);

        // sim slot
        sp_sim = PreferenceManager.getDefaultSharedPreferences(getActivity());
        simslot = sp_sim.getString("sim_preference", "0");

        // Recarga saldo
        binding.buttomRecargar.setOnClickListener(
                view -> {
                    if (!validateRecarga()) {
                        return;
                    } else {
                        String code = binding.editRecarga.getText().toString().trim();
                        SIMDialer.call(
                                getActivity(),
                                "*662*" + code + Uri.encode("#"),
                                Integer.parseInt(simslot));
                    }
                });

        // scanner qr recarga
        binding.inputRecarga.setEndIconOnClickListener(
                view -> {
                    ScanOptions scanner = new ScanOptions();
                    scanner.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
                    scanner.setPrompt(getString(R.string.message_scanner_qr_code));
                    scanner.setOrientationLocked(true);
                    barcodeLauncher.launch(scanner);
                });

        // transferir saldo
        binding.buttomTransferir.setOnClickListener(
                view -> {
                    if (!validateNumero()) {
                        return;
                    }
                    if (!validateMonto()) {
                        return;
                    }
                    if (!validateClave()) {
                        return;
                    } else {
                        String numero = binding.editNumero.getText().toString();
                        String clave = binding.editClave.getText().toString();
                        String monto = binding.editMonto.getText().toString();
                        SIMDialer.call(
                                getActivity(),
                                "*234*1*" + numero + "*" + clave + "*" + monto + Uri.encode("#"),
                                Integer.parseInt(simslot));
                    }
                });

        // select contact
        binding.inputNumero.setEndIconOnClickListener(
                view -> {
                    if (ContextCompat.checkSelfPermission(
                            getActivity(), Manifest.permission.READ_CONTACTS)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(
                                getActivity(),
                                new String[] {Manifest.permission.READ_CONTACTS},
                                20);
                        return;
                    } else {
                        pickContact.launch(null);
                    }
                });

        // --> Autocomplete clave transferir saldo
        SharedPreferences sp_autocomplete =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean active = sp_autocomplete.getBoolean("autocomplete", false);
        if (active) {
            binding.editClave.setText(sp_autocomplete.getString("password", "").toString());
        } else {
            binding.editClave.getText().clear();
        }

        // ---> Adelanta Saldo
        String[] monto = getResources().getStringArray(R.array.adelanta);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(
                        getActivity(), android.R.layout.simple_dropdown_item_1line, monto);
        binding.autocomplete.setAdapter(adapter);
        binding.buttomAdalanta.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (validateAdelanta()) {
                            String monto_adelanta =
                                    binding.autocomplete
                                            .getText()
                                            .toString()
                                            .replace("25.00 CUP", "25")
                                            .replace("50.00 CUP", "50")
                                            .trim();
                            SIMDialer.call(
                                    getActivity(),
                                    "*234*3*1*" + monto_adelanta + Uri.encode("#"),
                                    Integer.parseInt(simslot));
                        }
                    }
                });


        binding.sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext().startActivity(new Intent(getContext(), SmsActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        binding.voz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext().startActivity(new Intent(getContext(), VozActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        binding.PlanAmigos1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HomeFragment.PlanAmigosDialog();
            }
        });

        binding.emergancia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext().startActivity(new Intent(getContext(), EmergenciaActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
        binding.llamar99.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext().startActivity(new Intent(getContext(), Llamada_99Activity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        binding.llamarPrivado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext().startActivity(new Intent(getContext(), LlamadaOcultoActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }

        });

        // consulta saldo
        binding.saldo.setOnClickListener(
                view -> {
                    SIMDialer.call(
                            getActivity(), "*222" + Uri.encode("#"), Integer.parseInt(simslot));
                });

        // consulta bonos
        binding.buttomConsultaBonos.setOnClickListener(
                view -> {
                    SIMDialer.call(
                            getActivity(), "*222*266" + Uri.encode("#"), Integer.parseInt(simslot));
                });

        // consulta pospago
        binding.buttomConsultaPospago.setOnClickListener(
                view -> {
                    SIMDialer.call(
                            getActivity(), "*111" + Uri.encode("#"), Integer.parseInt(simslot));
                });

        // consulta datos
        binding.megas.setOnClickListener(
                view -> {
                    SIMDialer.call(
                            getActivity(), "*222*328" + Uri.encode("#"), Integer.parseInt(simslot));
                });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity()
                    .getWindow()
                    .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }
    }

    private boolean validateNumero() {
        if (binding.editNumero.getText().toString().trim().isEmpty()) {
            binding.inputNumero.setError(getString(R.string.validate_numero_empty));
            requestFocus(binding.editNumero);
            return false;
        } else if (binding.editNumero.getText().toString().length() < 8) {
            binding.inputNumero.setError(getString(R.string.validate_numero_digitos));
            requestFocus(binding.editNumero);
            return false;
        } else {
            binding.inputNumero.setErrorEnabled(true);
            binding.inputNumero.setError(null);
        }
        return true;
    }

    private boolean validateClave() {
        if (binding.editClave.getText().toString().trim().isEmpty()) {
            binding.inputClave.setError(getString(R.string.validate_numero_empty));
            //   requestFocus(binding.editClave);
            return false;
        } else if (binding.editClave.getText().toString().length() < 4) {
            binding.inputClave.setError(getString(R.string.validate_clave_digitos));
            //    requestFocus(binding.editClave);
            return false;
        } else {
            binding.inputClave.setError(null);
            binding.inputClave.setErrorEnabled(true);
        }
        return true;
    }

    private boolean validateMonto() {
        if (binding.editMonto.getText().toString().contains(".")) {
            binding.editMonto.getText().clear();
            Toast.makeText(getActivity(), getString(R.string.toast_centavos), Toast.LENGTH_LONG)
                    .show();
            requestFocus(binding.editMonto);
            return false;
        }
        return true;
    }

    private boolean validateRecarga() {
        if (binding.editRecarga.getText().toString().trim().isEmpty()) {
            binding.inputRecarga.setError(getString(R.string.validate_numero_empty));
            return false;
        } else if (binding.editRecarga.getText().toString().trim().length() < 16) {
            binding.inputRecarga.setError(getString(R.string.validate_clave_digitos));
            return false;
        }
        return true;
    }

    private boolean validateAdelanta() {
        if (binding.autocomplete.getText().toString().isEmpty()) {
            binding.inputAdelanta.setError("Seleccione la cantidad");
            return false;
        }
        return true;
    }

    private class ValidationTextWatcher implements TextWatcher {
        private View view;

        private ValidationTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.editNumero:
                    validateNumero();
                    break;
                case R.id.editClave:
                    validateClave();
                    break;
                case R.id.editMonto:
                    validateMonto();
                    break;
                case R.id.editRecarga:
                    validateRecarga();
                    break;
                case R.id.autocomplete:
                    validateAdelanta();
                    break;
            }
        }
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

    public class PlanAmigosDialog {

        public PlanAmigosDialog() {
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

    private void scannerNumero() {
        ScanOptions scanner = new ScanOptions();
        scanner.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        scanner.setPrompt(getString(R.string.message_scanner_numero));
        scanner.setOrientationLocked(true);
        scannerNumero.launch(scanner);
    }

    // scanner codigo recarga
    private final ActivityResultLauncher<ScanOptions> barcodeLauncher =
            registerForActivityResult(
                    new ScanContract(),
                    result -> {
                        if (result.getContents() == null) {
                            // ---> cancelado
                        } else {
                            String code = result.getContents().toString();
                            binding.editRecarga.setText(code);
                        }
                    });

    // scanner numero
    private final ActivityResultLauncher<ScanOptions> scannerNumero =
            registerForActivityResult(
                    new ScanContract(),
                    result -> {
                        if (result.getContents() == null) {
                            // ---> cancelado
                        } else {
                            String code = result.getContents().toString();
                            binding.editNumero.setText(code);
                        }
                    });

    // select contact
    private final ActivityResultLauncher<Void> pickContact =
            registerForActivityResult(
                    new ActivityResultContracts.PickContact(),
                    new ActivityResultCallback<Uri>() {
                        @SuppressLint("Range")
                        @Override
                        public void onActivityResult(Uri uri) {
                            if (uri != null) {
                                try {
                                    Cursor cursor =
                                            getActivity()
                                                    .getContentResolver()
                                                    .query(uri, null, null, null, null);
                                    if (cursor != null && cursor.getCount() > 0) {
                                        while (cursor.moveToNext()) {

                                            @SuppressLint("Range") String id =
                                                    cursor.getString(
                                                            cursor.getColumnIndex(
                                                                    ContactsContract.Contacts._ID));
                                            @SuppressLint("Range") String name =
                                                    cursor.getString(
                                                            cursor.getColumnIndex(
                                                                    ContactsContract.Contacts
                                                                            .DISPLAY_NAME));
                                            if (Integer.parseInt(
                                                    cursor.getString(
                                                            cursor.getColumnIndex(
                                                                    ContactsContract
                                                                            .Contacts
                                                                            .HAS_PHONE_NUMBER)))
                                                    > 0) {
                                                binding.inputNumero.setHelperText(name);

                                                // phone number
                                                Cursor phoneCursor =
                                                        getActivity()
                                                                .getContentResolver()
                                                                .query(
                                                                        ContactsContract
                                                                                .CommonDataKinds
                                                                                .Phone.CONTENT_URI,
                                                                        null,
                                                                        ContactsContract
                                                                                .CommonDataKinds
                                                                                .Phone
                                                                                .CONTACT_ID
                                                                                + " = ?",
                                                                        new String[] {id},
                                                                        null);
                                                while (phoneCursor.moveToNext()) {
                                                    @SuppressLint("Range") String number =
                                                            phoneCursor.getString(
                                                                    phoneCursor.getColumnIndex(
                                                                            ContactsContract
                                                                                    .CommonDataKinds
                                                                                    .Phone.NUMBER));
                                                    String replace =
                                                            number.replace("+53", "")
                                                                    .replace(" ", "")
                                                                    .replace("(", "")
                                                                    .replace(")", "")
                                                                    .replace("#", "")
                                                                    .replace("*", "");
                                                    replace =
                                                            replace.substring(replace.length() - 8);
                                                    if (replace.startsWith("5")
                                                            && replace.length() == 8) {
                                                        binding.editNumero.setText(replace);
                                                    } else {
                                                        Toast.makeText(
                                                                        getActivity(),
                                                                        name
                                                                                + " no es un número móvil",
                                                                        Toast.LENGTH_LONG)
                                                                .show();
                                                        binding.inputNumero.setHelperText(null);
                                                        binding.editNumero.setText(null);
                                                    }
                                                }
                                                phoneCursor.close();
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    e.getStackTrace();
                                    binding.inputNumero.setHelperText(null);
                                    binding.editNumero.setText(null);
                                }
                            }
                        }
                    });

    @SuppressWarnings("deprecation")
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_home, menu);
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem menuItem = menu.findItem(R.id.scann);
        menuItem.setVisible(true);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.scann:
                scannerNumero();
                break;
            default:
        }

        return super.onOptionsItemSelected(item);
    }
}