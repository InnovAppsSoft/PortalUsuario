package com.marlon.portalusuario.activities

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.marlon.portalusuario.R
import com.marlon.portalusuario.databinding.ActivityCreditosBinding

class AboutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreditosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreditosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
    }

    private fun initUI() {
        binding.javierFacebook.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.setData(Uri.parse("https://facebook.com/javyalejandro99"))
            startActivity(i)
        }
        binding.javierInsta.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.setData(Uri.parse("https://www.instagram.com/jalexoasismusic/?hl=es"))
            startActivity(i)
        }
        binding.javierTwitter.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.setData(Uri.parse("https://twitter.com/javyalejandro99"))
            startActivity(i)
        }
        binding.javierTelegram.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.setData(Uri.parse("https://t.me/jalexcode"))
            startActivity(i)
        }
        binding.javierTelegramChannel.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.setData(Uri.parse("https://t.me/oasismusicofficial"))
            startActivity(i)
        }
        binding.javierGithub.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.setData(Uri.parse("https://github.com/jalexcode"))
            startActivity(i)
        }

        /*making personal info visible*/
        binding.experience.visibility = View.VISIBLE
        binding.review.visibility = View.GONE
        binding.experiencebtn.setOnClickListener {
            binding.experience.visibility = View.VISIBLE
            binding.review.visibility = View.GONE
            binding.experiencebtn.setTextColor(resources.getColor(R.color.blue))
            binding.reviewbtn.setTextColor(resources.getColor(R.color.colorDes))
        }
        binding.reviewbtn.setOnClickListener {
            binding.experience.visibility = View.GONE
            binding.review.visibility = View.VISIBLE
            binding.experiencebtn.setTextColor(resources.getColor(R.color.colorDes))
            binding.reviewbtn.setTextColor(resources.getColor(R.color.blue))
        }
        binding.virarhaciatras.setOnClickListener { finish() }
        binding.google.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.marlon.portalusuario"))
            startActivity(i)
        }
        binding.apklis.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.setData(Uri.parse("https://www.apklis.cu/application/com.marlon.portalusuario"))
            startActivity(i)
        }
        binding.politicadeprivacidad.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.setData(Uri.parse("https://m.apkpure.com/es/portal-usuario/com.marlon.portalusuario"))
            startActivity(i)
        }
        val version = findViewById<TextView>(R.id.version)
        var pinfo: PackageInfo? = null
        try {
            pinfo = applicationContext.packageManager.getPackageInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        val versionName = pinfo!!.versionName
        version.text = versionName
    }
}
