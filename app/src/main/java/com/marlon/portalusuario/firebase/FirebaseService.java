package com.marlon.portalusuario.firebase;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.marlon.portalusuario.MainActivity;
import com.marlon.portalusuario.PUNotifications.PUNotification;
import com.marlon.portalusuario.PUNotifications.PUNotificationsActivity;
import com.marlon.portalusuario.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.GregorianCalendar;

public class FirebaseService extends FirebaseMessagingService {

    private NotificationManagerCompat notificationManagerCompat;
    private static final String TAG = "MyFirebaseMsgService";
    private SharedPreferences sharedPreferences;

    @Override
    public void onNewToken (String token){
        super.onNewToken(token);
    }

    // [START receive_message]
    @Override
    public final void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            try {
                Log.e(TAG, "Message Notification Title: " + remoteMessage.getNotification().getTitle());
                Log.e(TAG, "Message Notification Image Icon: " + remoteMessage.getNotification().getImageUrl());
                Log.e(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            }catch (Exception ex){
                ex.printStackTrace();
            }
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            //
            boolean storageADS = sharedPreferences.getBoolean("storage_ads", true);
            Log.e("STORAGE ADS", String.valueOf(storageADS));
            String title = remoteMessage.getNotification().getTitle() != null ? remoteMessage.getNotification().getTitle() : "";
            String body = remoteMessage.getNotification().getBody() != null ? remoteMessage.getNotification().getBody() : "";
            String imageUrl = "";
            if (remoteMessage.getNotification().getImageUrl() != null){
                imageUrl = remoteMessage.getNotification().getImageUrl().toString();
            }
            final PUNotification pun = new PUNotification(title, body, imageUrl, new GregorianCalendar().getTimeInMillis());
            if (storageADS) {
                String image = pun.getImage();
                if (image != null && !image.isEmpty()) {
                    try {
                        Glide.with(getApplicationContext())
                                .asBitmap()
                                .load(image)
                                .into(new CustomTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        //largeIcon
                                        //builder.setLargeIcon(resource);
                                        //Big Picture
                                        //builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(resource));
                                        //
                                        int start = image.lastIndexOf("/");
                                        String filename = image.substring(start + 1);
                                        String filepath = getApplicationContext().getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()).getAbsolutePath() + "/pun/" + filename;
                                        File file = new File(filepath);
                                        Log.e("IMAGE FILEPATH", filepath);
                                        OutputStream os = null;
                                        try {
                                            try {
                                                if (!file.exists()) {
                                                    file.createNewFile();
                                                }
                                            } catch (IOException ex) {
                                                ex.printStackTrace();
                                            }
                                            os = new FileOutputStream(file);
                                            resource.compress(Bitmap.CompressFormat.JPEG, 100, os);
                                        } catch (FileNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                        pun.setImage(filepath);
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {
                                    }

                                    @Override
                                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                        super.onLoadFailed(errorDrawable);
                                    }
                                });
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                //
                MainActivity.insertNotification(pun);
                //
            }
            sendNotification(pun);

        }
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

//    }
    @SuppressLint("NewApi")
    public void sendNotification(PUNotification pun){
        if (sharedPreferences.getBoolean("show_notifications", true)) {
            // INTENT QUE LANZARA LA ACTIVITY
            // PUEDE MODIFICARSE PARA LANZAR CUALQUIER OTRA ACTIVIDAD
            // DEBE MODIFICARSE LA JERARQUIA DE ACTIVITIES EN EL MANIFIESTO
            Intent resultIntent = new Intent(this, PUNotificationsActivity.class);
            // CREAR TaskStackBuilder Y ANEXAR INTENT AL FINAL DE LA COLA DE LLAMADAS
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addNextIntentWithParentStack(resultIntent);
            // OBTENER EL INTENT PENDIENTE A MOSTRAR
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT);
            // SI ES VERSION ANDROID 8 EN ADELANTE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // CREA CANAL DE NOTIFIACION
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                NotificationChannel channel = new NotificationChannel("PUNoticias", "Mensajes de Portal Usuario", NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            // CREAR NOTIFIACION
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "PUNoticias")
                    .setSmallIcon(getNotificationIcon())
                    .setContentTitle(pun.getTitle())
                    .setContentText(pun.getTitle())
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(false)
                    .setSound(defaultSoundUri)
                    .setLights(Color.BLUE, 500, 500)
                    .setVibrate(new long[]{500, 500, 500})
                    .setContentIntent(resultPendingIntent)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            //We only need to call this for SDK 26+, since startForeground always has to be called after startForegroundService.
            //startForeground(NOTIFICATION_ID, builder.build());
            notificationManagerCompat = NotificationManagerCompat.from(this);
            notificationManagerCompat.notify(0, builder.build());
        }
    }
    private int getNotificationIcon(){
        return R.drawable.ic_event;
    }

}