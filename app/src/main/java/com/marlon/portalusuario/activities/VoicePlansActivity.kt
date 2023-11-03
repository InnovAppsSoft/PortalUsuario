package com.marlon.portalusuario.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.marlon.portalusuario.R
import com.marlon.portalusuario.databinding.ActivityVoicePlansBinding

private const val RequestCode = 1000

class VoicePlansActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityVoicePlansBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVoicePlansBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val adRequest = AdRequest.Builder().build()
        val adView = AdView(this)
        adView.setAdSize(AdSize.BANNER)
        adView.adUnitId = "ca-app-pub-9665109922019776/1173610479"
        binding.adViewVoice.loadAd(adRequest)
        binding.adViewVoice.adListener = object : AdListener() {
            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                // Code to be executed when an ad request fails.
            }

            override fun onAdImpression() {
                // Code to be executed when an impression is recorded
                // for an ad.
            }

            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }
        }

        binding.fiveMinutes.setOnClickListener(this)
        binding.tenMinutes.setOnClickListener(this)
        binding.fifteenMinutes.setOnClickListener(this)
        binding.twentyFiveMinutes.setOnClickListener(this)
        binding.fortyMinutes.setOnClickListener(this)
        binding.minutesGoBack.setOnClickListener { super@VoicePlansActivity.onBackPressed() }
    }

    @SuppressLint("NonConstantResourceId")
    override fun onClick(v: View) {
        when (v.id) {
            R.id.five_minutes -> { makeCall("*133*3*1%23") }
            R.id.ten_minutes -> { makeCall("*133*3*2%23") }
            R.id.fifteen_minutes -> { makeCall("*133*3*3%23") }
            R.id.twenty_five_minutes -> { makeCall("*133*3*4%23") }
            R.id.forty_minutes -> { makeCall("*133*3*5%23") }
        }
    }

    private fun makeCall(ussd: String) {
        val r = Intent()
        r.setAction(Intent.ACTION_CALL)
        r.setData(Uri.parse("tel:$ussd"))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), RequestCode)
            } else {
                startActivity(r)
            }
        } else {
            startActivity(r)
        }
    }
}
