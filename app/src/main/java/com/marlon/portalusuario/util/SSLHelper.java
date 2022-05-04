package com.marlon.portalusuario.util;

import android.annotation.SuppressLint;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class SSLHelper {
    static public Connection getConnection(String url){

        return Jsoup.connect(url).sslSocketFactory(SSLHelper.socketFactory());
    }
    static private SSLSocketFactory socketFactory(){

        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager(){

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            @SuppressLint("TrustAllX509TrustManager")
            @Override
            public void checkClientTrusted(X509Certificate[] certs, String authType) {

            }

            @SuppressLint("TrustAllX509TrustManager")
            @Override
            public void checkServerTrusted(X509Certificate[] certs, String authType){

            }


        } };

        try{
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            return sslContext.getSocketFactory();


        }catch (NoSuchAlgorithmException | KeyManagementException e){
            throw new RuntimeException("Failed to create a SSL socket factory", e);
        }

    }
}
