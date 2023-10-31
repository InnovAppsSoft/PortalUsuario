package com.marlon.portalusuario.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.marlon.portalusuario.databinding.ActivityEmergencyCallsBinding

private const val RequestCode = 1000

class EmergencyCallsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmergencyCallsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityEmergencyCallsBinding.inflate(
            layoutInflater
        )
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val adRequest = AdRequest.Builder().build()
        val adView = AdView(this)
        adView.setAdSize(AdSize.BANNER)
        adView.adUnitId = "ca-app-pub-9665109922019776/1173610479"
        configureAdView(adRequest)
        initUI()
    }

    private fun initUI() {
        binding.goBack.setOnClickListener { super@EmergencyCallsActivity.onBackPressed() }
        binding.ambulance.setOnClickListener { launchCall("104") }
        binding.police.setOnClickListener { launchCall("106") }
        binding.firefighters.setOnClickListener { launchCall("105") }
        binding.antiDrugs.setOnClickListener { launchCall("103") }
        binding.maritimeRescue.setOnClickListener { launchCall("107") }
        binding.cubacel.setOnClickListener { launchCall("52642266") }
    }

    private fun configureAdView(adRequest: AdRequest) {
        binding.adViewsemes.loadAd(adRequest)
        binding.adViewsemes.adListener = object : AdListener() {
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
    }

    private fun launchCall(ussd: String) {
        val r = Intent()
            .setAction(Intent.ACTION_CALL)
            .setData(Uri.parse("tel:$ussd"))
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
