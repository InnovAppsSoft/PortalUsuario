package com.marlon.portalusuario.erroreslog

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.marlon.portalusuario.R

class LogAdapter(
    private val logsList: List<String>,
    private val context: Context,
) : RecyclerView.Adapter<LogAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.log_item, parent, false),
        )
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        val logText = logsList[position]
        holder.tvLog.text = logText

        val color =
            when {
                logText.contains("| E |") -> Color.RED
                logText.contains("| W |") -> Color.rgb(255, 152, 0)
                else -> Color.rgb(100, 100, 100)
            }
        holder.tvLog.setTextColor(color)

        holder.tvLog.setOnLongClickListener {
            val msg = logsList[position]
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("log", msg))
            Toast.makeText(context, "LOG copiado al portapapeles", Toast.LENGTH_SHORT).show()
            true
        }
    }

    override fun getItemCount(): Int = logsList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvLog: TextView = itemView.findViewById(R.id.tvLog)
    }
}
