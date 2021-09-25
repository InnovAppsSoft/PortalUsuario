package com.marlon.portalusuario.senal;

import android.annotation.SuppressLint;
import android.app.Service;

import android.content.Intent;

import android.os.IBinder;


import androidx.annotation.Nullable;


@SuppressLint("Registered")
public class Servicio extends Service {
    private static boolean D = false;
    long A;
    long B;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static boolean j() {
        return D;
    }
}