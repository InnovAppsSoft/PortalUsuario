package com.marlon.portalusuario.PUNotifications;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
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
import com.marlon.portalusuario.R;
import com.marlon.portalusuario.util.Util;

import java.util.GregorianCalendar;
import java.util.List;

import co.dift.ui.SwipeToAction;

public class PUNAdapter extends RecyclerView.Adapter<PUNAdapter.PUNViewHolder> {
    private Context context;
    private List<PUNotification> notificationsList;

    public PUNAdapter(List<PUNotification> notifications, Context context) {
        notificationsList = notifications;
        this.context = context;
    }

    @NonNull
    @Override
    public PUNViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PUNViewHolder(LayoutInflater.from(context).inflate(R.layout.post_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PUNViewHolder holder, int position) {
        PUNotification pun = notificationsList.get(position);
        String title = pun.getTitle();
        if (!title.isEmpty()) {
            holder.titleTv.setText(title);
        }else {
            holder.titleTv.setText("Portal Usuario anuncia...!");
        }
        //
        String details = pun.getText();
        if (!details.isEmpty()) {
            holder.detailsTv.setText(details);
        }else{
            holder.detailsTv.setText("Sin detalles");
        }
        //
        GregorianCalendar date = Util.long2Date(pun.getDate());
        if (date != null) {
            holder.dateTv.setText(pun.dateToString());
        }else{
            holder.dateTv.setText("Sin fecha");
        }
        String image = pun.getImage();
        if (image != null && !image.isEmpty()){
            Log.e("IMAGE ADAPTER", image);
            Glide.with(context).load(image)
                    .centerCrop()
                    .into(holder.circleImage);
            Glide.with(context).load(image)
                    .centerCrop()
                    .into(holder.bigImage);
        }
        holder.arrowBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if (holder.expandableView.getVisibility()==View.GONE){
                    TransitionManager.beginDelayedTransition(holder.cardView, new AutoTransition());
                    holder.expandableView.setVisibility(View.VISIBLE);
                    holder.circleImageContainer.setVisibility(View.GONE);
                    holder.arrowBtn.setRotation(180);
                    //
                } else {
                    TransitionManager.beginDelayedTransition(holder.cardView, new AutoTransition());
                    holder.circleImageContainer.setVisibility(View.VISIBLE);
                    holder.arrowBtn.setRotation(0);
                    holder.expandableView.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationsList.size();
    }

    class PUNViewHolder extends RecyclerView.ViewHolder {//SwipeToAction.ViewHolder {
        private ImageView circleImage;
        private TextView titleTv;
        private TextView dateTv;
        private TextView detailsTv;
        private Button arrowBtn;
        private ConstraintLayout expandableView;
        private CardView cardView;
        private ImageView bigImage;
        private CardView circleImageContainer;

        public PUNViewHolder(View itemView) {
            super(itemView);
            arrowBtn = itemView.findViewById(R.id.arrowBtn);
            circleImageContainer = itemView.findViewById(R.id.view);
            bigImage = itemView.findViewById(R.id.big_image);
            cardView = itemView.findViewById(R.id.cardView);
            expandableView = itemView.findViewById(R.id.expandableView);
            circleImage = itemView.findViewById(R.id.circleImage);
            titleTv = itemView.findViewById(R.id.title);
            dateTv = itemView.findViewById(R.id.date);
            detailsTv = itemView.findViewById(R.id.description);
        }
    }
}
