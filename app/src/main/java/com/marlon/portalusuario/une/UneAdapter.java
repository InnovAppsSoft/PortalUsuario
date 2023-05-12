package com.marlon.portalusuario.une;

import android.content.Context;
import android.os.Build;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionListenerAdapter;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.marlon.portalusuario.une.Une;
import com.marlon.portalusuario.R;
import com.marlon.portalusuario.util.Util;

import java.util.GregorianCalendar;
import java.util.List;

public class UneAdapter extends RecyclerView.Adapter<UneAdapter.UneViewHolder> {
    private final Context context;
    private final List<Une> registersList;

    public UneAdapter(List<Une> notifications, Context context) {
        registersList = notifications;
        this.context = context;
    }

    @NonNull
    @Override
    public UneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UneViewHolder(LayoutInflater.from(context).inflate(R.layout.une_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UneViewHolder holder, int position) {
        Une une = registersList.get(position);

        double lastRegister = une.getLastRegister();
        holder.previousRead.setText("Lectura anterior: " + lastRegister);
        //
        double currentRegister = une.getCurrentRegister();
        holder.currentRead.setText("Lectura actual: " + currentRegister);
        //
        double totalConsumption = une.getTotalConsumption();
        holder.totalConsumption.setText("Consumo: " + totalConsumption + " Kw/h");
        //
        double totalToPay = une.getTotalToPay();
        holder.totalToPay.setText("$ " + totalToPay);
        //
        long dateLongType = une.getDate();
        if (dateLongType != 0) {
            GregorianCalendar date = Util.long2Date(dateLongType);
            holder.cardView.setOnClickListener(v -> Toast.makeText(context, Util.date2String(date), Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    public int getItemCount() {
        return registersList.size();
    }

    class UneViewHolder extends RecyclerView.ViewHolder {
        private final TextView currentRead;
        private final TextView previousRead;
        private final TextView totalConsumption;
        private final TextView totalToPay;
        private final CardView cardView;

        public UneViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            currentRead = itemView.findViewById(R.id.current_read);
            previousRead = itemView.findViewById(R.id.previous_read);
            totalConsumption = itemView.findViewById(R.id.total_consumption);
            totalToPay = itemView.findViewById(R.id.total_to_pay);
        }
    }
}
