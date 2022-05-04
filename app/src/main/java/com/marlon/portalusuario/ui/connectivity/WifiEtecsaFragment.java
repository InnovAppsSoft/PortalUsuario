package com.marlon.portalusuario.ui.connectivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ActionViewTarget;
import com.google.android.material.textfield.TextInputEditText;
import com.marlon.portalusuario.PortalNautaActivity;
import com.marlon.portalusuario.ViewModel.UserViewModel;
import com.marlon.portalusuario.logging.JCLogging;
import com.marlon.portalusuario.R;
import com.marlon.portalusuario.User;
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
import soup.neumorphism.NeumorphButton;
import trikita.log.Log;


public class WifiEtecsaFragment extends Fragment {
    private final int CUENTA_NACIONAL = 1;
    private final String CUENTA_NACIONAL_STR = "Cuenta Nacional";
    private final int CUENTA_INTERNACIONAL = 0;
    private final String CUENTA_INTERNACIONAL_STR = "Cuenta Internacional";

    private Context context;
    private SharedPreferences pref;
    TextView errorsTextView;
    public User my_user;
    public boolean user_passw_error;
    private Spinner selectUserSpinner;
    private ImageView addUserBtn;
    private ImageView editUserBtn;
    private ImageView delUserBtn;
    private CheckBox connectionLimiter;
    private Spinner limiterType;
    private EditText limiterTime;
    private NeumorphButton connectBtn;
    private NeumorphButton userInfoBtn;
    public LinearLayout errorLayout;
    private LottieAnimationView lottieAnimationView;

    //private AutoCompleteTextView usernameEditText;
    private EditText usernameEditText;
    private TextInputEditText passwordEditText;
    private Spinner accountTypeSpinner;
    private TextView noUsers, info;
    private EditText captchaEditText;
    private Button reloadCaptcha;
    private ImageView captchaImg;


    private ProgressDialog loadingBar;

    private UserViewModel userViewModel;

    private List<User> userList;

    private int currentUserInt = 0;

    private ArrayList<String> accountTypes;

    private JCLogging logger;

    Map<String, String> cookies;
    Map<Object, Object> status;
    UserPortal userPortal = new UserPortal();
    private boolean success = false;
    private StringBuilder errors = new StringBuilder();

    String _time;
    String account;
    String block_date;
    String delete_date;
    String account_type;
    String service_type;
    String credit;
    String mail_account;
    Intent transitionIntent;

    public WifiEtecsaFragment() {
        // Required empty public constructor
    }

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
        //
        connectBtn = view.findViewById(R.id.connect_button);
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
        lottieAnimationView = view.findViewById(R.id.error_lottie);
        loadingBar = new ProgressDialog(getContext());

        user_passw_error = false;

        my_user = User.getUser();

        // BUTTON CONNECTION
        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendConnect();
            }
        });

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
        //
        return view;
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

    public class EditUserDialog {
        public EditUserDialog(final Context context, User user) {
            View inflate = getLayoutInflater().inflate(R.layout.nauta_accounts_edit_dialog, (ViewGroup) null, false);
            AlertDialog.Builder editUserDialog = new AlertDialog.Builder(context);
            editUserDialog.setCancelable(true)
            .setView(inflate)
            .setTitle("Editar usuario")
            .setIcon(R.drawable.baseline_account_circle_black_48);

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
                if (!username.matches("[A-Za-z0-9_]+")){
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

    private void sendConnect()  {
        if (Util.isConnected(context)) {
            loadingBar.setIcon(R.mipmap.ic_launcher);
            loadingBar.setMessage("Conectando, por favor, espere...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String str = "td";
                        String str2 = "";
                        @SuppressLint("SimpleDateFormat") String format = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime());
                        Connection.Response execute = Jsoup.connect("https://secure.etecsa.net:8443").method(Connection.Method.GET).execute();

                        my_user.setCSRFHW(execute.parse().select("input[name=CSRFHW]").first().val());
                        Log.e("Current Username", currentUser().getFullUsername());
                        logger.message("Current Username", currentUser().getFullUsername());
                        my_user.setUsername(currentUser().getUsername().toString());
                        my_user.setPassword(currentUser().getPassword().toString());

                        Document post = Jsoup.connect("https://secure.etecsa.net:8443/EtecsaQueryServlet").cookies(execute.cookies())
                                .data("wlanacname", str2).data("wlanmac", str2).data("firsturl", "notFound.jsp")
                                .data("ssid", str2).data("usertype", str2).data("gotopage", "/nauta_etecsa/LoginURL/mobile_login.jsp")
                                .data("successpage", "/nauta_etecsa/OnlineURL/mobile_index.jsp")
                                .data("loggerId", format).data("lang", "es_ES").data("username", my_user.getFullUsername())
                                .data("password", my_user.getPassword())
                                .data("CSRFHW", my_user.getCSRFHW()).followRedirects(true).post();
                        if (!post.select("script").last().toString().contains("alert(\"return null\");")) {
                            logger.message("Session info table", post.select("table#sessioninfo > tbody > tr > td").size());
                            my_user.setAccountCredit(post.select("table#sessioninfo > tbody > tr > td").get(3).text());
                            my_user.setAccountState(post.select("table#sessioninfo > tbody > tr > td").get(1).text());

                            Document loggin = Jsoup.connect("https://secure.etecsa.net:8443/LoginServlet")
                                    .data("username", my_user.getFullUsername()).data("password", my_user.getPassword()).followRedirects(true).post();

                            my_user.setATTRIBUTE_UUID(loggin.select("script").first().toString().split("ATTRIBUTE_UUID=")[1].split("&")[0]);

                            System.out.println(my_user);
                            sendMessage(null);
                        } else {
                            user_passw_error = true;
                        }

                    } catch (Exception e) {
                        errors.append("error: ").append(e.getMessage()).append("\n");
                        logger.error(null, null, e);
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (errors.length() > 0){
                                Toast.makeText(context, "No se pudo conectar", Toast.LENGTH_SHORT).show();
                                setErrorMessage("Ha ocurrido error", R.raw.wronganswer);
                            }
                            if (user_passw_error) {
                                setErrorMessage("Usuario o clave incorrecta", R.raw.wronganswer);
                            }
                            loadingBar.dismiss();
                        }
                    });

                }
            }).start();
        }else{
            Toast.makeText(context, "No está conectado", Toast.LENGTH_SHORT).show();
        }

    }

    /** Called when the user taps the Send button */
    public void sendMessage(View view) {
        Intent intent = new Intent(getContext(), WifiAccountDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", my_user);
        int maxTime = 0;
        String timeType = "";
        if (connectionLimiter.isChecked()){
            maxTime = Integer.parseInt(limiterTime.getText().toString());
            timeType = limiterType.getSelectedItem().toString();
            bundle.putInt("max_time", maxTime);
            bundle.putString("time_type", timeType);
        }
        intent.putExtras(bundle);
        //
        SharedPreferences.Editor prefEditor = pref.edit();
        prefEditor.putBoolean("connected", true);
        prefEditor.apply();
        //
        Log.e("LLEGUE", "PAPU");
        startActivity(intent);
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
                success = login();
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
        lottieAnimationView.setAnimation(r);
        lottieAnimationView.playAnimation();
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
}