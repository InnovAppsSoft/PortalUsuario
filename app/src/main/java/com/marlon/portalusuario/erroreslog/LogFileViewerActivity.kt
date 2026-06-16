package com.marlon.portalusuario.erroreslog

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.marlon.portalusuario.R
import java.io.File
import java.io.IOException

class LogFileViewerActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var logAdapter: LogAdapter
    private lateinit var errorMessage: TextView
    private lateinit var loadingBar: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logs)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Registro de depuración"
        }

        JCLogging.init(this)
        recyclerView = findViewById(R.id.rvLogs)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        errorMessage = findViewById(R.id.tvNoLogs)
        loadingBar =
            ProgressDialog(this).apply {
                setMessage("Cargando archivo de Logs")
                setCanceledOnTouchOutside(false)
            }

        refreshLog()
    }

    private fun refreshLog() {
        LoadLogsTask().execute()
    }

    override fun onResume() {
        super.onResume()
        refreshLog()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    @SuppressLint("StaticFieldLeak")
    private inner class LoadLogsTask : AsyncTask<Void, Void, List<String>?>() {
        override fun onPreExecute() {
            super.onPreExecute()
            loadingBar.show()
            recyclerView.visibility = View.INVISIBLE
            errorMessage.visibility = View.INVISIBLE
        }

        override fun doInBackground(vararg params: Void): List<String>? {
            val file = File(JCLogging.getDirectory(), "log.txt")
            if (!file.exists()) return emptyList()
            return try {
                JCLogging.readFromFile(file)
            } catch (ex: IOException) {
                JCLogging.error(null, null, ex)
                null
            }
        }

        override fun onPostExecute(result: List<String>?) {
            super.onPostExecute(result)
            recyclerView.visibility = View.INVISIBLE
            when {
                result == null -> {
                    errorMessage.visibility = View.VISIBLE
                    errorMessage.text = "No existe archivo de registro"
                }
                result.isEmpty() -> {
                    errorMessage.visibility = View.VISIBLE
                    errorMessage.text = "El archivo de registro está vacío"
                }
                else -> {
                    errorMessage.visibility = View.INVISIBLE
                    setAdapter(result)
                    recyclerView.visibility = View.VISIBLE
                }
            }
            loadingBar.dismiss()
        }
    }

    private fun setAdapter(logs: List<String>) {
        logAdapter = LogAdapter(logs, this)
        recyclerView.adapter = logAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_log, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_refresh -> {
                refreshLog()
                return true
            }
            R.id.menu_share_log -> {
                try {
                    val log =
                        JCLogging.readAllFromFile(File(JCLogging.getDirectory(), "log.txt"))
                    val clipboard =
                        getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("log", log))
                    Toast
                        .makeText(
                            this,
                            "Registro copiado al portapapeles",
                            Toast.LENGTH_SHORT,
                        ).show()

                    val share =
                        Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_SUBJECT,
                                "Registro de actividades de Portal Usuario",
                            )
                            putExtra(Intent.EXTRA_TEXT, log)
                        }
                    startActivity(
                        Intent.createChooser(share, "Enviar registro de Portal Usuario"),
                    )
                } catch (e: IOException) {
                    e.printStackTrace()
                    JCLogging.error(null, null, e)
                }
                return true
            }
            R.id.menu_go_to_final -> {
                val pos = logAdapter.itemCount
                if (pos > 0) {
                    recyclerView.scrollToPosition(pos - 1)
                }
                return true
            }
            R.id.menu_clear -> {
                AlertDialog
                    .Builder(this)
                    .setMessage(R.string.msg_sure)
                    .setPositiveButton(android.R.string.yes) { _, _ ->
                        JCLogging.clearLog()
                        refreshLog()
                        Toast
                            .makeText(
                                this,
                                "Archivo de registro limpiado",
                                Toast.LENGTH_LONG,
                            ).show()
                    }.setNegativeButton(android.R.string.no, null)
                    .show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
