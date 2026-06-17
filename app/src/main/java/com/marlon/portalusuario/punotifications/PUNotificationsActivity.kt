package com.marlon.portalusuario.punotifications

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.marlon.portalusuario.R
import com.marlon.portalusuario.util.Util
import com.marlon.portalusuario.viewmodel.PunViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PUNotificationsActivity : AppCompatActivity() {
    private lateinit var punViewModel: PunViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var noPostLayout: LinearLayout

    private var notifications: List<PUNotification>? = null

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_punotifications)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.default_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        noPostLayout = findViewById(R.id.no_posts)

        val status = findViewById<TextView>(R.id.status_text_view)
        val statusTxt = findViewById<TextView>(R.id.action_bar_title_2)
        if (Util.isConnected(this)) {
            status.setBackgroundResource(R.drawable.online_circle)
            statusTxt.text = "En línea"
        } else {
            status.setBackgroundResource(R.drawable.offline_circle)
            statusTxt.text = "Desconectado"
        }

        recyclerView = findViewById(R.id.recycler_notifications)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        punViewModel = ViewModelProvider(this)[PunViewModel::class.java]
        punViewModel.allPUNLiveData.observe(this) { puNotifications ->
            if (puNotifications.isNotEmpty()) {
                noPostLayout.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
            Log.e("CANTIDAD DE NOTIF", puNotifications.size.toString())
            setAdapter(puNotifications)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun setAdapter(notifications: List<PUNotification>) {
        val punAdapter = PUNAdapter(notifications, this)
        recyclerView.adapter = punAdapter
        recyclerView.scheduleLayoutAnimation()
    }
}
