package com.marlon.portalusuario.errores_log;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.marlon.portalusuario.R;

import java.util.List;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.ViewHolder> {
    private final Context context;
    private final List<String> logsList;

    public LogAdapter(List<String> logs, Context context) {
        this.logsList = logs;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.log_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String logText = logsList.get(position);
        holder.tvLog.setText(logText);
        //
        int color;
        if (logText.contains("| E |")) {
            color = Color.RED;
        } else if (logText.contains("| W |")) {
            color = Color.rgb(255, 152, 0);
        } else {
            color = Color.rgb(100, 100, 100);
        }
        holder.tvLog.setTextColor(color);
        //
        holder.tvLog.setOnLongClickListener(new View.OnLongClickListener() {
            @SuppressLint("ShowToast")
            @Override
            public boolean onLongClick(View v) {
                String msg = logsList.get(position);
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("log", msg);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, "LOG copiado al portapapeles", Toast.LENGTH_SHORT);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return logsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvLog;

        public ViewHolder(View itemView) {
            super(itemView);
            tvLog = itemView.findViewById(R.id.tvLog);
        }
    }
}
