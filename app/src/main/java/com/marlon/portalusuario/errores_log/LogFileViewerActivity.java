// JalexCode - LogFileViewer //
package com.marlon.portalusuario.errores_log;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.marlon.portalusuario.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LogFileViewerActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private LogAdapter logAdapter;
    private TextView errorMessage;
    private ProgressDialog loadingBar;
    private JCLogging JCLogging;

    public LogFileViewerActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);
        //
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Registro de depuración");
        }
        // INIT DE ELEMENTOS
        this.setTitle("Registro de depuración");
        this.setTitleColor(Color.GREEN);
        JCLogging = new JCLogging(LogFileViewerActivity.this);
        recyclerView = findViewById(R.id.rvLogs);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        errorMessage = findViewById(R.id.tvNoLogs);
        loadingBar = new ProgressDialog(LogFileViewerActivity.this);
        loadingBar.setMessage("Cargando archivo de Logs");
        loadingBar.setCanceledOnTouchOutside(false);
        //
        refresh_log();
    }

    private void refresh_log(){
        new loadLogs().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh_log();
    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @SuppressLint("StaticFieldLeak")
    private class loadLogs extends AsyncTask<Void, Void, List<String>> {

        public void onPreExecute() {
            super.onPreExecute();
            //
            loadingBar.show();
            //
            recyclerView.setVisibility(View.INVISIBLE);
            errorMessage.setVisibility(View.INVISIBLE);
            //
        }

        public List<String> doInBackground(Void... voidArr) {
            File file = new File(com.marlon.portalusuario.errores_log.JCLogging.getDirectory(), "log.txt");
            if (!file.exists()) {
                return new ArrayList<>();
            }
            try {
                return com.marlon.portalusuario.errores_log.JCLogging.readFromFile(file);
            } catch (IOException ex) {
                com.marlon.portalusuario.errores_log.JCLogging.error(null, null,ex);
                return null;
            }
        }

        public void onPostExecute(List<String> list) {
            super.onPostExecute(list);
            recyclerView.setVisibility(View.INVISIBLE);
            if (list == null) {
                errorMessage.setVisibility(View.VISIBLE);
                errorMessage.setText("No existe archivo de registro");
            } else if (list.isEmpty()) {
                errorMessage.setVisibility(View.VISIBLE);
                errorMessage.setText("El archivo de registro está vacío");
            } else {
                errorMessage.setVisibility(View.INVISIBLE);
                setAdapter(list);
                recyclerView.setVisibility(View.VISIBLE);
            }
            loadingBar.dismiss();
        }
    }

    private void setAdapter(List<String> logs) {
        this.logAdapter = new LogAdapter(logs, LogFileViewerActivity.this);
        this.recyclerView.setAdapter(this.logAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_log, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem refresh = menu.findItem(R.id.menu_refresh);
        MenuItem copy_all = menu.findItem(R.id.menu_share_log);
        MenuItem go_to_final = menu.findItem(R.id.menu_go_to_final);
        MenuItem clear = menu.findItem(R.id.menu_clear);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int item2 = item.getItemId();
        if (item2 == R.id.menu_refresh) {
            refresh_log();
            return true;

        }else if (item2 == R.id.menu_share_log) {
            try {
                String log = com.marlon.portalusuario.errores_log.JCLogging.readAllFromFile(new File(com.marlon.portalusuario.errores_log.JCLogging.getDirectory(), "log.txt"));
                ClipboardManager clipboard = (ClipboardManager) LogFileViewerActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("log", log);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(LogFileViewerActivity.this, "Registro copiado al portapapeles", Toast.LENGTH_SHORT);
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_SUBJECT, "Registro de actividades de Portal Usuario");
                share.putExtra(Intent.EXTRA_TEXT, log);
                startActivity(Intent.createChooser(share, "Enviar registro de Portal Usuario"));
            } catch (IOException e) {
                e.printStackTrace();
                com.marlon.portalusuario.errores_log.JCLogging.error(null, null, e);
            }
            return true;

        }else if(item2 == R.id.menu_go_to_final) {
            if (logAdapter != null) {
                int pos = logAdapter.getItemCount();
                if (pos > -1) {
                    recyclerView.scrollToPosition(pos - 1);
                }
            }
            return true;

        }else if (item2 == R.id.menu_clear) {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.msg_sure)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            com.marlon.portalusuario.errores_log.JCLogging.clearLog();
                            refresh_log();
                            Toast.makeText(LogFileViewerActivity.this, "Archivo de registro limpiado", Toast.LENGTH_LONG);
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
            return true;
        }else {
                return super.onOptionsItemSelected(item);
        }
    }
}