package com.marlon.portalusuario.PUNotifications;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.marlon.portalusuario.R;
import com.marlon.portalusuario.ViewModel.PunViewModel;
import com.marlon.portalusuario.database.notifications.PunDAO;

import java.util.List;

import co.dift.ui.SwipeToAction;

public class PUNotificationsActivity extends AppCompatActivity {
    private PunViewModel punViewModel;
    private RecyclerView recyclerView;
    private LinearLayout noPostLayout;

    private List<PUNotification> notifications;
    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
    private PunDAO dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_punotifications);

        Toolbar toolbar = findViewById(R.id.default_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        noPostLayout = findViewById(R.id.no_posts);

        TextView status = findViewById(R.id.status_text_view);
        TextView statusTxt = findViewById(R.id.action_bar_title_2);
        if (com.marlon.portalusuario.util.Util.isConnected(this)){
            status.setBackgroundResource(R.drawable.online_circle);
            statusTxt.setText("En línea");
        }else{
            status.setBackgroundResource(R.drawable.offline_circle);
            statusTxt.setText("Desconectado");
        }
        recyclerView = findViewById(R.id.recycler_notifications);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //
        // observer
        punViewModel = new ViewModelProvider(this).get(PunViewModel.class);
        punViewModel.getAllPUNotifications().observe(this, new Observer<List<PUNotification>>() {
            @Override
            public void onChanged(List<PUNotification> puNotifications) {
                if (!puNotifications.isEmpty()){
                    noPostLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
                Log.e("CANTIDAD DE NOTIF", String.valueOf(puNotifications.size()));
                setAdapter(puNotifications);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void setAdapter(List<PUNotification> notifications) {
        PUNAdapter punAdapter = new PUNAdapter(notifications, this);
        recyclerView.setAdapter(punAdapter);
        recyclerView.scheduleLayoutAnimation();
        //
        SwipeToAction swipeToAction = new SwipeToAction(recyclerView, new SwipeToAction.SwipeListener<PUNAdapter>() {
            @Override
            public boolean swipeLeft(final PUNAdapter itemData) {
                //do something
                return true; //true will move the front view to its starting position
            }

            @Override
            public boolean swipeRight(PUNAdapter itemData) {
                //do something
                return true;
            }

            @Override
            public void onClick(PUNAdapter itemData) {
                //do something
            }

            @Override
            public void onLongClick(PUNAdapter itemData) {
                //do something
            }
        });
    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_p_u_notifications, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        return super.onPrepareOptionsMenu(menu);
//    }
//
//    @SuppressLint("NonConstantResourceId")
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.delete_notifications:
//                if (notifications.isEmpty()){
//                    Toast.makeText(this, "No hay mensajes almacenados", Toast.LENGTH_SHORT).show();
//                    return true;
//                }
//                new AlertDialog.Builder(this)
//                        .setMessage(R.string.msg_sure)
//                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dbHandler.deleteAllPUNotification();
//                                Toast.makeText(PUNotificationsActivity.this, "Mensajes eliminados", Toast.LENGTH_LONG).show();
//                                refresh();
//                            }
//                        })
//                        .setNegativeButton(android.R.string.no, null)
//                        .show();
//                break;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//        return true;
//    }
//
//    private void refresh() {
//    }
}