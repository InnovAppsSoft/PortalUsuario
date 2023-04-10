package com.marlon.portalusuario.view.activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.marlon.portalusuario.R;

public class AboutActivity extends AppCompatActivity {

    private LinearLayout personalinfo, experience, review, Google,Apklis,Politica;
    private TextView experiencebtn, reviewbtn, javierFacebook,
    javierTwitter, javierInsta, javierGitHub, javierTelegram, javierTelegramChannel;

    private ImageView Back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creditos);

        experience = findViewById(R.id.experience);
        review = findViewById(R.id.review);
        experiencebtn = findViewById(R.id.experiencebtn);
        reviewbtn = findViewById(R.id.reviewbtn);

        Back = (ImageView)findViewById(R.id.virarhaciatras);

        //
        javierFacebook = (TextView) findViewById(R.id.javier_facebook);
        javierTwitter = (TextView) findViewById(R.id.javier_twitter);
        javierInsta = (TextView) findViewById(R.id.javier_insta);
        javierGitHub = (TextView) findViewById(R.id.javier_github);
        javierTelegram = (TextView) findViewById(R.id.javier_telegram);
        javierTelegramChannel = (TextView) findViewById(R.id.javier_canal_telegram);

        Google = (LinearLayout) findViewById(R.id.google);
        Apklis = (LinearLayout) findViewById(R.id.apklis);
        Politica = (LinearLayout) findViewById(R.id.politicadeprivacidad);

        javierFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tlr = ("https://facebook.com/javyalejandro99");

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(tlr));
                startActivity(i);
            }
        });

        javierInsta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tlr = ("https://www.instagram.com/jalexoasismusic/?hl=es");//"https://m.facebook.com/JalexCode-Solutions-101249345625126/?ref=bookmarks");

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(tlr));
                startActivity(i);
            }
        });

        javierTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tlr = ("https://twitter.com/javyalejandro99");

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(tlr));
                startActivity(i);
            }
        });

        javierTelegram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tlr = ("https://t.me/jalexcode");

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(tlr));
                startActivity(i);
            }
        });

        javierTelegramChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tlr = ("https://t.me/oasismusicofficial");

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(tlr));
                startActivity(i);
            }
        });

        javierGitHub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tlr = ("https://github.com/jalexcode");

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(tlr));
                startActivity(i);
            }
        });

        /*making personal info visible*/
        experience.setVisibility(View.VISIBLE);
        review.setVisibility(View.GONE);

        experiencebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                experience.setVisibility(View.VISIBLE);
                review.setVisibility(View.GONE);
                experiencebtn.setTextColor(getResources().getColor(R.color.blue));
                reviewbtn.setTextColor(getResources().getColor(R.color.colorDes));

            }
        });

        reviewbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                experience.setVisibility(View.GONE);
                review.setVisibility(View.VISIBLE);
                experiencebtn.setTextColor(getResources().getColor(R.color.colorDes));
                reviewbtn.setTextColor(getResources().getColor(R.color.blue));
            }
        });

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AboutActivity.this.finish();
            }
        });

        Google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String google = ("https://play.google.com/store/apps/details?id=com.marlon.portalusuario");

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(google));
                startActivity(i);
            }
        });

        Apklis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String apklis = ("https://www.apklis.cu/application/com.marlon.portalusuario");

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(apklis));
                startActivity(i);
            }
        });

        Politica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String politic = ("https://m.apkpure.com/es/portal-usuario/com.marlon.portalusuario");

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(politic));
                startActivity(i);
            }
        });


        TextView version = findViewById(R.id.version);
        PackageInfo pinfo = null;
        try {
            pinfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String versionName = pinfo.versionName;
        version.setText(versionName);
        }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}

