package com.marlon.portalusuario.view.Fragments.connectivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import android.os.CountDownTimer;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.textfield.TextInputEditText;
import com.marlon.portalusuario.view.activities.PortalNautaActivity;
import com.marlon.portalusuario.ViewModel.UserViewModel;
import com.marlon.portalusuario.logging.JCLogging;
import com.marlon.portalusuario.R;
import com.marlon.portalusuario.model.User;
import com.marlon.portalusuario.net.Communicator;
import com.marlon.portalusuario.net.RunTask;
import com.marlon.portalusuario.util.Util;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import cu.marilasoft.selibrary.UserPortal;
import cu.suitetecsa.kotlibsuitetecsa.NautaSession;
import soup.neumorphism.NeumorphButton;
import trikita.log.Log;


public class WifiEtecsaFragment extends Fragment {
    private final int CUENTA_NACIONAL = 1;
    private final String CUENTA_NACIONAL_STR = "Cuenta Nacional";
    private final int CUENTA_INTERNACIONAL = 0;
    private final String CUENTA_INTERNACIONAL_STR = "Cuenta Internacional";

    private Context context;
    private SharedPreferences pref;

    NautaSession nautaSession;

    private TextView errorsTextView;
    private Spinner selectUserSpinner;
    private ImageView addUserBtn, editUserBtn, delUserBtn;
    private CheckBox connectionLimiter;
    private Spinner limiterType;
    private EditText limiterTime;

    private Button  logOutBtn,connectBtn,userInfoBtn;
    public LinearLayout errorLayout;
    private ConstraintLayout sessionInfoLayout;

    //private AutoCompleteTextView usernameEditText;
    private EditText usernameEditText, captchaEditText;
    private TextInputEditText passwordEditText;
    private Spinner accountTypeSpinner;
    private TextView noUsers, info;
    private Button reloadCaptcha;
    private ImageView captchaImg;

    private ProgressDialog loadingBar;

    private UserViewModel userViewModel;

    private List<User> userList = new ArrayList<>();

    private int currentUserInt = 0;

    private ArrayList<String> accountTypes;

    private JCLogging logger;

    Map<String, String> cookies;
    Map<Object, Object> status;
    UserPortal userPortal = new UserPortal();
    private StringBuilder errors = new StringBuilder();
    TextView saldo, leftTime, estadoCuenta;
    public Button btnDisconnect;

    private boolean firstTime = true;
    private int initialH = 0;
    private int initialM = 0;
    private int initialS = 0;

    private int maxTime;
    private String timeType;
    private Chronometer simpleChronometer;

    String _time;
    String account;
    String block_date;
    String delete_date;
    String account_type;
    String service_type;
    String credit;
    String mail_account;
    Intent transitionIntent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logger = new JCLogging(getContext());
        pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        // ACCOUNT TYPES ARRAY
        accountTypes = new ArrayList<>();
        accountTypes.add(CUENTA_INTERNACIONAL_STR);
        accountTypes.add(CUENTA_NACIONAL_STR);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wifi_etecsa, container, false);
        //
        initiViewModel();
        initUi(view);
        //
        return view;
    }

    private void initiViewModel(){
        // VIEW MODEL
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.getAllUsers().observe(getViewLifecycleOwner(), user -> {
            if (!user.isEmpty()) {
                isUserListEmpty(false);
                userList = user;
                ArrayAdapter<User> spinnerAdapter = new UsersSpinnerAdapter();
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                selectUserSpinner.setAdapter(spinnerAdapter);
            }else{
                isUserListEmpty(true);
            }
        });
    }

    private void initUi(View view){
        //
        logOutBtn = view.findViewById(R.id.logout_button);
        logOutBtn.setOnClickListener(v -> sendDisconnect());
        connectBtn = view.findViewById(R.id.connect_button);
        connectBtn.setOnClickListener(v -> sendConnect(view));
        //
        sessionInfoLayout = view.findViewById(R.id.sessionInfoLayout);
        //
        userInfoBtn = view.findViewById(R.id.user_info_button);
        userInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new FastInfoUserDialog(context, currentUser());
            }
        });
        //
        selectUserSpinner = view.findViewById(R.id.select_user_spinner);
        selectUserSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currentUserInt = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //
        addUserBtn = view.findViewById(R.id.add_user_btn);
        addUserBtn.setOnClickListener(view1 -> new EditUserDialog(context, null));
        editUserBtn = view.findViewById(R.id.edit_user_btn);
        editUserBtn.setOnClickListener(view14 -> new EditUserDialog(context, currentUser()));
        delUserBtn = view.findViewById(R.id.del_user_btn);
        delUserBtn.setOnClickListener(view13 -> {
            new AlertDialog.Builder(context)
                    .setMessage("¿Desea eliminar este usuario?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            userViewModel.deleteUser(currentUser());
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        });
        connectionLimiter = view.findViewById(R.id.connection_limiter);
        connectionLimiter.setOnCheckedChangeListener((compoundButton, b) -> {
            LinearLayout limiterParamsLayout = view.findViewById(R.id.limiter_params_layout);
            if (b){
                limiterParamsLayout.setVisibility(View.VISIBLE);
            }else{
                limiterParamsLayout.setVisibility(View.GONE);
            }
        });
        // LIMITER TYPE
        limiterType = view.findViewById(R.id.limiter_type);
        // ACCOUNT TYPES ARRAY
        ArrayList limiterTypes = new ArrayList<>();
        limiterTypes.add("horas");
        limiterTypes.add("minutos");
        limiterTypes.add("segundos");
        ArrayAdapter<String> limiterTypeSpinnerAdapter  = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, limiterTypes);
        limiterType.setAdapter(limiterTypeSpinnerAdapter);
        //
        limiterTime = view.findViewById(R.id.limiter_time);
        noUsers = view.findViewById(R.id.no_users);
        errorsTextView = view.findViewById(R.id.error_tv);
        errorLayout = view.findViewById(R.id.error_layout);
        errorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (errors.length() > 0) {
                    AlertDialog.Builder errorDialog = new AlertDialog.Builder(context);
                    errorDialog.setCancelable(true)
                            .setTitle("Errores")
                            .setIcon(R.drawable.error)
                            .setMessage(errors.toString());
                    errorDialog.show();
                }
            }
        });
        loadingBar = new ProgressDialog(getContext());
        saldo = view.findViewById(R.id.editSaldoCuenta);
        leftTime = view.findViewById(R.id.textLeftTime);
        estadoCuenta = view.findViewById(R.id.editEstadoCuenta);
        btnDisconnect = view.findViewById(R.id.buttonDisconnect);
    }

    private void initChrono(View view){
        simpleChronometer = view.findViewById(R.id.simpleChronometer); // initiate a chronometer
        simpleChronometer.setOnChronometerTickListener(cArg -> {
            long time = SystemClock.elapsedRealtime() - cArg.getBase();
            int h = (int)(time /3600000);
            int m = (int)(time - h*3600000)/60000;
            int s = (int)(time - h*3600000- m*60000)/1000 ;
            String hh = h < 10 ? "0"+h: h+"";
            String mm = m < 10 ? "0"+m: m+"";
            String ss = s < 10 ? "0"+s: s+"";
            String formated_time = String.format("%s:%s:%s", hh, mm, ss);
            if (firstTime){
                initialH = h;
                initialM = m;
                initialS = s;
                firstTime = false;
            }
//            if (prefs.getBoolean("show_traffic_speed_bubble", false)) {
//                MainActivity.setConnectedTime(formated_time);
//            }
            cArg.setText(formated_time);
            //
            try {
                if (maxTime > 0) {
                    if (timeType.equals("horas")) {
                        if (h - initialH == maxTime) {
                            sendDisconnect();
                            return;
                        }
                    } else if (timeType.equals("minutos")) {
                        if (m - initialM == maxTime) {
                            sendDisconnect();
                            return;
                        }
                    } else if (timeType.equals("segundos")) {
                        if (s - initialS == maxTime) {
                            sendDisconnect();
                            return;
                        }
                    }
                }
            }catch (Exception ex){
                ex.printStackTrace();
                logger.error(null, null, ex);
            }
        });
        simpleChronometer.setBase(SystemClock.elapsedRealtime());
        simpleChronometer.start();
    }

    private void setUserDataOnDashboard(){
        saldo.setText(currentUser().getAccountCredit());
        estadoCuenta.setText(currentUser().getAccountState());
    }

    private void sendConnect(View view)  {
        if (Util.isConnected(context)) {
            loadingBar.setIcon(R.mipmap.ic_launcher);
            loadingBar.setMessage("Conectando...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            new Thread(() -> {
                try {
                    nautaSession = new NautaSession();
                    nautaSession.init();
                    //
                    loadingBar.setMessage("Iniciando sesión...");
                    String username = currentUser().getUsername();
                    String passw = currentUser().getPassword();
                    //
                    nautaSession.login(username, passw, nautaSession.getCookies());
                    //
                    setUserDataOnDashboard();
                    initChrono(view);
                    sendLeftTime();
                    //
                    connectBtn.setVisibility(View.GONE);
                    logOutBtn.setVisibility(View.VISIBLE);
                    sessionInfoLayout.setVisibility(View.VISIBLE);
                    //
                    loadingBar.dismiss();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            });
        }else{
            Toast.makeText(context, "No está conectado", Toast.LENGTH_SHORT).show();
        }
    }

    public void Disconnect(View v) {
        sendDisconnect();
    }

    public void countDown(){
        String time = nautaSession.getUserTime(currentUser().getUsername(), nautaSession.getCookies());
        logger.message("Initial time value", time);
        long milisecondsLeft = 0;
        //
        try {
            milisecondsLeft = Integer.parseInt(time.split(":")[0]) * 60 * 60 * 1000 + Integer.parseInt(time.split(":")[1]) * 60 * 1000 + Integer.parseInt(time.split(":")[2]) * 1000;
        }catch (Exception e){
            e.printStackTrace();
            logger.error(null, null, null);
            logger.error(null, null, e);
        }
        new CountDownTimer(milisecondsLeft, 1000) {
            public void onTick(long millisUntilFinished) {
                int h   = (int)(millisUntilFinished /3600000);
                int m = (int)(millisUntilFinished - h*3600000)/60000;
                int s= (int)(millisUntilFinished - h*3600000- m*60000)/1000 ;
                String hh = h < 10 ? "0"+h: h+"";
                String mm = m < 10 ? "0"+m: m+"";
                String ss = s < 10 ? "0"+s: s+"";
                leftTime.setText(String.format("%s:%s:%s", hh, mm, ss));
            }

            public void onFinish() {
                leftTime.setText("00:00:00");
            }
        }.start();
    }

    public void sendLeftTime() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                countDown();
            }

        }).start();
    }
    //
    public void sendDisconnect() {
        loadingBar.setTitle("Desconectando");
        loadingBar.setMessage("Por favor espere....");
        loadingBar.setIcon(R.mipmap.ic_launcher);
        loadingBar.setCanceledOnTouchOutside(true);
        loadingBar.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                nautaSession.logout(currentUser().getUsername(), nautaSession.getCookies());
                // hidding loading bar
                loadingBar.dismiss();
                // update UI
                connectBtn.setVisibility(View.VISIBLE);
                logOutBtn.setVisibility(View.GONE);
                sessionInfoLayout.setVisibility(View.GONE);
                // toast
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "Desconectado", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    public class UsersSpinnerAdapter extends ArrayAdapter<User> {
        public UsersSpinnerAdapter() {
            super(context.getApplicationContext(), R.layout.user_item_layout, userList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.user_item_layout, parent, false);
            }

            User user = userList.get(position);

            TextView username = convertView.findViewById(R.id.username);
            TextView accountType = convertView.findViewById(R.id.account_type);

            username.setText(user.getUsername());
            accountType.setText(accountTypes.get(user.getAccountNavegationType()));
            return convertView;
        }
    }

    /* DIALOGS */

    public class EditUserDialog {
        public EditUserDialog(final Context context, User user) {
            View inflate = getLayoutInflater().inflate(R.layout.nauta_accounts_edit_dialog, (ViewGroup) null, false);
            AlertDialog.Builder editUserDialog = new AlertDialog.Builder(context);
            editUserDialog.setCancelable(true)
            .setView(inflate)
            .setTitle("Editar usuario")
            .setIcon(R.drawable.round_person_24);

            usernameEditText = inflate.findViewById(R.id.username_et);
            passwordEditText = inflate.findViewById(R.id.password_et);
            accountTypeSpinner = inflate.findViewById(R.id.account_type_spinner);
            info = inflate.findViewById(R.id.info);
            info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "El nombre de usuario no puede contener el nombre de dominio @nauta.com.cu\nEjemplo: Si su cuenta es juan@nauta.com.cu, solo debe poner juan", Toast.LENGTH_LONG).show();
                }
            });

//            AutoCompleteAdapter adapter = new AutoCompleteAdapter(getContext(), android.R.layout.simple_expandable_list_item_1);
//            usernameEditText.setAdapter(adapter);

            ArrayAdapter<String> accountTypeSpinnerAdapter  = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, accountTypes);
            accountTypeSpinner.setAdapter(accountTypeSpinnerAdapter);

            if (user != null){
                usernameEditText.setText(user.getUsername());
                passwordEditText.setText(user.getPassword());
                accountTypeSpinner.setSelection(user.getAccountNavegationType());
            }

            editUserDialog.setPositiveButton("Guardar", (dialogInterface, i) -> {
                String username = usernameEditText.getText().toString();
                String passw = passwordEditText.getText().toString();
                if (!username.matches("[A-Za-z0-9_.@]+")){
                    Toast.makeText(context, "El nombre de usuario no puede contener símbolos", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (user == null) {
                    userViewModel.insertUser(new User(username, passw, "", "", "", accountTypeSpinner.getSelectedItemPosition(), "", "", "", 0, "", "", "", "", ""));
                }else{
                    user.setUsername(username);
                    user.setPassword(passw);
                    user.setAccountNavegationType(accountTypeSpinner.getSelectedItemPosition());
                    userViewModel.updateUser(user);
                }
            });
            editUserDialog.setNegativeButton("Cancelar", null);

            editUserDialog.show();
        }
    }

    public class FastInfoUserDialog {
        public FastInfoUserDialog(Context context, User user){
            String info = "";
            String lastUpdate = pref.getString("last_portal_nauta_update", "");
            info += "Última actualización: " + (!lastUpdate.isEmpty() ? lastUpdate : "-") + "\n";
            info += "Usuario: " + user.getFullUsername() + "\n";
            info += "Cuenta de correo asociada: " + (user.getEmailAccount().isEmpty() ? "-" : user.getEmailAccount()) + "\n";
            info += "Tipo de cuenta: " + (user.getAccountNavegationType() == CUENTA_INTERNACIONAL ? CUENTA_INTERNACIONAL_STR : CUENTA_NACIONAL_STR)  + "\n";
            info += "Crédito: " + (user.getAccountCredit() != "" ? user.getAccountCredit() : "-")+ "\n";
            info += "Tiempo restante: " + (user.getLeftTime() != "" ? user.getLeftTime() : "-") + "\n";
            info += "Última conexión: " + (user.getLastConnectionDateTime() != 0 ? Util.date2String(Util.long2Date(user.getLastConnectionDateTime())) : "-") + "\n";
            AlertDialog.Builder fastInfoUserDialog = new AlertDialog.Builder(context);
            fastInfoUserDialog.setCancelable(true)
                    .setMessage(info)
                    .setTitle("Información de usuario")
                    .setIcon(R.drawable.info)
                    .setPositiveButton("Portal Nauta", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            new CaptchaUserDialog(context, user);
                        }
                    })
                    .setNegativeButton("Atrás", null);
            fastInfoUserDialog.show();
        }

    }

    public class CaptchaUserDialog {
        private LinearLayout progressLayout;
        private LinearLayout errorLayout;
        private LinearLayout captchaParamsLayout;
        public CaptchaUserDialog(final Context context, User user) {
            View inflate = getLayoutInflater().inflate(R.layout.captcha_dialog, null, false);
            AlertDialog.Builder editUserDialog = new AlertDialog.Builder(context);
            editUserDialog.setCancelable(true)
                    .setView(inflate)
                    .setTitle("Escriba el código Captcha")
                    .setIcon(R.drawable.ic_security)
                    .setPositiveButton("Conectar", (dialogInterface, i) -> this.login())
                    .setNegativeButton("Cancelar", null);
            reLoadCaptcha();
            //
            progressLayout = inflate.findViewById(R.id.progress_layout);
            errorLayout = inflate.findViewById(R.id.error_layout);
            captchaParamsLayout = inflate.findViewById(R.id.captcha_params_layout);
            captchaEditText = inflate.findViewById(R.id.captcha_et);
            reloadCaptcha = inflate.findViewById(R.id.bt_reload);
            reloadCaptcha.setOnClickListener(view -> reLoadCaptcha());
            captchaImg = inflate.findViewById(R.id.iv_captcha);

            editUserDialog.setPositiveButton("Conectar", (dialogInterface, i) -> {
                login();
            });

            editUserDialog.show();
        }

        private void setCaptchaLoaded(boolean b){
            if (b){
                progressLayout.setVisibility(View.GONE);
                captchaImg.setVisibility(View.VISIBLE);
                captchaParamsLayout.setVisibility(View.VISIBLE);
            }else{
                progressLayout.setVisibility(View.VISIBLE);
                captchaImg.setVisibility(View.GONE);
                captchaParamsLayout.setVisibility(View.GONE);
            }
        }

        private void setError(boolean b){
            if (b){
                progressLayout.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
            }else{
                progressLayout.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
            }
        }

        public void reLoadCaptcha() {
            new RunTask(new Communicator() {
                @Override
                public void Communicate() {

                }

                @Override
                public void Communicate(UserPortal userPortal) {
                    setCaptchaLoaded(false);
                    try {
                        if (cookies == null) {
                            userPortal.preLogin();
                            cookies = userPortal.cookies();
                            userPortal = userPortal;
                        }
                        userPortal.loadCAPTCHA(cookies);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void communicate() {
                    if (userPortal.captchaImg() != null) {
                        Bitmap bitmap = BitmapFactory.decodeStream(
                                new ByteArrayInputStream(userPortal.captchaImg())
                        );
                        setCaptchaLoaded(true);
                        captchaImg.setImageBitmap(bitmap);
                    } else {
                        setError(true);
                        Toast.makeText(getContext(),
                                "No se pudo cargar la imagen CAPTCHA", Toast.LENGTH_LONG).show();

                    }
                }
            }, userPortal).execute();
        }

        public void preLoad() {
            new RunTask(new Communicator() {
                @Override
                public void Communicate() {

                }

                @Override
                public void Communicate(UserPortal userPortal) {
                    try {
                        userPortal.preLogin();
                        Map<String, String> cookies = userPortal.cookies();
                        userPortal.loadCAPTCHA(cookies);
                        cookies = cookies;
                        userPortal = userPortal;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void communicate() {
                    if (userPortal.captchaImg() != null) {
                        Bitmap bitmap = BitmapFactory.decodeStream(
                                new ByteArrayInputStream(userPortal.captchaImg())
                        );
                        captchaImg.setImageBitmap(bitmap);
                    } else {
                        Toast.makeText(getContext(),
                                "No se pudo cargar la imagen CAPTCHA", Toast.LENGTH_LONG).show();
                    }
                }
            }, userPortal).execute();
        }

        public boolean login() {
            loadingBar.setIcon(R.drawable.ic_wifi);
            loadingBar.setMessage("Conectando, por favor, espere...");
            loadingBar.setCancelable(false);
            loadingBar.setCanceledOnTouchOutside(false);

            int validate = 0;
            if (captchaEditText.getText().toString().equals("")) {
//                captchaEditText.setError("Introduzca el código captcha.");
                Toast.makeText(context, "Introduzca el código captcha", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (validate != 1) {
                if (Util.isConnected(context)) {
                    loadingBar.show();
                    new RunTask(new Communicator() {
                        @Override
                        public void Communicate() {

                        }

                        @Override
                        public void Communicate(UserPortal userPortal) {
                            try {
                                User current = currentUser();
                                String username = current.getFullUsername();
                                String password = current.getPassword();
                                userPortal.login(username,
                                        password,
                                        captchaEditText.getText().toString(),
                                        cookies);
                                status = userPortal.status();
                                if (userPortal.status().get("status").equals("success")) {
                                    account = userPortal.userName();
                                    _time = userPortal.time();
                                    block_date = userPortal.blockDate();
                                    delete_date = userPortal.delDate();
                                    account_type = userPortal.accountType();
                                    service_type = userPortal.serviceType();
                                    credit = userPortal.credit();
                                    mail_account = userPortal.mailAccount();
                                    //
                                    //
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                logger.error(null, null, e);
                                loadingBar.dismiss();
                            }
                        }

                        @Override
                        public void communicate() {
                            String errors = "";
                            try {
                                loadingBar.dismiss();
                                if (status.get("status").equals("success")) {
                                    transitionIntent = new Intent(context, PortalNautaActivity.class);
                                    transitionIntent.putExtra("userName",
                                            account);
                                    transitionIntent.putExtra("password",
                                            currentUser().getPassword());
                                    transitionIntent.putExtra("blockDate",
                                            block_date);
                                    transitionIntent.putExtra("delDate",
                                            delete_date);
                                    transitionIntent.putExtra("accountType",
                                            account_type);
                                    transitionIntent.putExtra("serviceType",
                                            service_type);
                                    transitionIntent.putExtra("credit",
                                            credit);
                                    transitionIntent.putExtra("time",
                                            _time);
                                    transitionIntent.putExtra("mailAccount",
                                            mail_account);
                                    transitionIntent.putExtra("session",
                                            cookies.get("session"));
                                    transitionIntent.putExtra("nauta_lang",
                                            cookies.get("nauta_lang"));
                                    //
                                    User user = currentUser();
                                    user.setBlockDate(block_date);
                                    user.setDelDate(delete_date);
                                    user.setAccountType(account_type);
                                    user.setServiceType(service_type);
                                    user.setAccountCredit(credit);
                                    user.setLeftTime(_time);
                                    user.setEmailAccount(mail_account);
                                    userViewModel.updateUser(user);
                                    //
                                    SharedPreferences.Editor prefEditor = pref.edit();
                                    prefEditor.putString("last_portal_nauta_update", Util.date2String(Util.currentDate()));
                                    prefEditor.apply();
                                    //
                                    startActivity(transitionIntent);
                                } else if (status.get("status").equals("error")) {
                                    for (String error : (List<String>) status.get("msg")) {
                                        errors += "\n" + error;
                                        if (error.contains("Usuario")) {
                                            errors += "Puede que el nombre de usuario sea incorrecto\n";
                                        }
                                        if (error.contains("contraseña")) {
                                            errors += "Puede que la contraseña sea incorrecta\n";
                                        }
                                        if (error.contains("Captcha")) {
                                            captchaEditText.setError(error);
                                        }
                                    }
                                    reLoadCaptcha();
                                }
                            } catch (Exception ex) {
                                setErrorMessage("Ha ocurrido un error :-(", R.raw.wronganswer);
                                ex.printStackTrace();
                                Toast.makeText(getContext(), "Ha ocurrido un error :-(\n" + errors, Toast.LENGTH_LONG).show();
                                logger.error(null, null, ex);
                            }
                        }
                    }, userPortal).execute();
                }else{
                    Toast.makeText(context, "No está conectado", Toast.LENGTH_SHORT).show();
                }
            }
            return true;
        }
    }

    public void setErrorMessage(String message, int r){
        errorLayout.setVisibility(View.VISIBLE);
        errorsTextView.setText(message);
    }

    @Override
    public void onResume() {
        Log.e("Wifi", "onResume of Wifi");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.e("Wifi", "OnPause of Wifi");
        super.onPause();
    }

    private void isUserListEmpty(boolean b){
        if (b){
            noUsers.setVisibility(View.VISIBLE);
            selectUserSpinner.setVisibility(View.GONE);
            editUserBtn.setVisibility(View.GONE);
            delUserBtn.setVisibility(View.GONE);
            userInfoBtn.setVisibility(View.GONE);
            connectBtn.setVisibility(View.GONE);
            connectionLimiter.setChecked(false);
            connectionLimiter.setVisibility(View.GONE);
            setErrorMessage("Agrega al menos un usuario para usar esta funcionalidad", R.raw.userlogin);
        }else{
            noUsers.setVisibility(View.GONE);
            selectUserSpinner.setVisibility(View.VISIBLE);
            editUserBtn.setVisibility(View.VISIBLE);
            delUserBtn.setVisibility(View.VISIBLE);
            userInfoBtn.setVisibility(View.VISIBLE);
            connectBtn.setVisibility(View.VISIBLE);
            connectionLimiter.setVisibility(View.VISIBLE);
            errorLayout.setVisibility(View.GONE);
        }
    }

    private User currentUser(){
        return userList.get(currentUserInt);
    }
}