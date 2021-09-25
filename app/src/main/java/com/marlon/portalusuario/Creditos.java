package com.marlon.portalusuario;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.marlon.portalusuario.datastats.WidgetActivity;

public class Creditos extends AppCompatActivity {

    private LinearLayout personalinfo, experience, review, Google,Apklis,Politica,BagData;
    private TextView personalinfobtn, experiencebtn, reviewbtn,Facebook,Instagram,Twitter;
    private Button Javier,Telegram;


    private ImageView Virar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creditos);

        personalinfo = findViewById(R.id.personalinfo);
        experience = findViewById(R.id.experience);
        review = findViewById(R.id.review);
        personalinfobtn = findViewById(R.id.personalinfobtn);
        experiencebtn = findViewById(R.id.experiencebtn);
        reviewbtn = findViewById(R.id.reviewbtn);

        Facebook = (TextView) findViewById(R.id.facebook);
        Instagram = (TextView) findViewById(R.id.instagram);
        Twitter = (TextView) findViewById(R.id.twitter);
        Virar = (ImageView)findViewById(R.id.virarhaciatras);

        Google = (LinearLayout) findViewById(R.id.google);
        Apklis = (LinearLayout) findViewById(R.id.apklis);
        Politica = (LinearLayout) findViewById(R.id.politicadeprivacidad);
        BagData = (LinearLayout) findViewById(R.id.bagdata);
        Javier = (Button) findViewById(R.id.donarjavier);
        Telegram = (Button) findViewById(R.id.telegram);


        Telegram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tlr = ("https://t.me/jalexcodesolutions");

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(tlr));
                startActivity(i);
            }
        });


        Javier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Creditos.this, JavierActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });



        /*making personal info visible*/
        personalinfo.setVisibility(View.VISIBLE);
        experience.setVisibility(View.GONE);
        review.setVisibility(View.GONE);


        personalinfobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                personalinfo.setVisibility(View.VISIBLE);
                experience.setVisibility(View.GONE);
                review.setVisibility(View.GONE);
                personalinfobtn.setTextColor(getResources().getColor(R.color.blue));
                experiencebtn.setTextColor(getResources().getColor(R.color.colorDes));
                reviewbtn.setTextColor(getResources().getColor(R.color.colorDes));
            }
        });

        experiencebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                personalinfo.setVisibility(View.GONE);
                experience.setVisibility(View.VISIBLE);
                review.setVisibility(View.GONE);
                personalinfobtn.setTextColor(getResources().getColor(R.color.colorDes));
                experiencebtn.setTextColor(getResources().getColor(R.color.blue));
                reviewbtn.setTextColor(getResources().getColor(R.color.colorDes));

            }
        });

        reviewbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                personalinfo.setVisibility(View.GONE);
                experience.setVisibility(View.GONE);
                review.setVisibility(View.VISIBLE);
                personalinfobtn.setTextColor(getResources().getColor(R.color.colorDes));
                experiencebtn.setTextColor(getResources().getColor(R.color.colorDes));
                reviewbtn.setTextColor(getResources().getColor(R.color.blue));
            }
        });

        Facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String face = ("https://www.facebook.com/marlondejesus99");

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(face));
                startActivity(i);
            }
        });

        Instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inta = ("https://www.instagram.com/marlitho_o/?hl=es");

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(inta));
                startActivity(i);
            }
        });

        Virar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Creditos.this.finish();
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

        Twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String twitt = ("https://twitter.com/Marlitho_o");

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(twitt));
                startActivity(i);
            }
        });

        BagData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bagdata = ("https://play.google.com/store/apps/details?id=com.marlon.trustatlas");

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(bagdata));
                startActivity(i);

            }

            });

        }

    @Override
    public void onBackPressed() {
        Creditos.super.onBackPressed();
    }

}

