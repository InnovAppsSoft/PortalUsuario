package com.marlon.portalusuario.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.marlon.portalusuario.R
import com.marlon.portalusuario.databinding.ActivityCreditosBinding

class AboutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreditosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreditosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
    }

    private fun setupUI() {
        setupSocialMediaLinks()
        setupInfoVisibilityToggle()
        setupNavigation()
        setupAppLinks()
        setAppVersion()
    }

    private fun setupInfoVisibilityToggle() {
        // Initialize views state
        binding.experience.visibility = View.VISIBLE
        binding.review.visibility = View.GONE
        updateButtonColors(binding.experiencebtn, binding.reviewbtn, true)

        binding.experiencebtn.setOnClickListener {
            binding.experience.visibility = View.VISIBLE
            binding.review.visibility = View.GONE
            updateButtonColors(binding.experiencebtn, binding.reviewbtn, true)
        }
        binding.reviewbtn.setOnClickListener {
            binding.experience.visibility = View.GONE
            binding.review.visibility = View.VISIBLE
            updateButtonColors(binding.experiencebtn, binding.reviewbtn, false)
        }
    }

    private fun updateButtonColors(experienceButton: View, reviewButton: View, experienceActive: Boolean) {
        val activeColor = ContextCompat.getColor(this, R.color.blue)
        val inactiveColor = ContextCompat.getColor(this, R.color.colorDes)

        experienceButton.findViewById<android.widget.TextView>(R.id.experiencebtn).setTextColor(if (experienceActive) activeColor else inactiveColor)
        reviewButton.findViewById<android.widget.TextView>(R.id.reviewbtn).setTextColor(if (!experienceActive) activeColor else inactiveColor)
    }

    private fun setupNavigation() {
        binding.virarhaciatras.setOnClickListener { finish() }
    }

    private fun setupAppLinks() {
        val appLinks = mapOf(
            binding.google to "https://play.google.com/store/apps/details?id=com.marlon.portalusuario",
            binding.apklis to "https://www.apklis.cu/application/com.marlon.portalusuario",
            binding.politicadeprivacidad to "https://m.apkpure.com/es/portal-usuario/com.marlon.portalusuario"
        )

        appLinks.forEach { (view, url) ->
            view.setOnClickListener { openLinkInBrowser(url) }
        }
    }

    private fun setupSocialMediaLinks() {
        val socialLinks = mapOf(
            binding.javierFacebook to "https://facebook.com/javyalejandro99",
            binding.javierInsta to "https://www.instagram.com/jalexoasismusic/?hl=es",
            binding.javierTwitter to "https://twitter.com/javyalejandro99",
            binding.javierTelegram to "https://t.me/jalexcode",
            binding.javierTelegramChannel to "https://t.me/oasismusicofficial",
            binding.javierGithub to "https://github.com/jalexcode"
        )

        socialLinks.forEach { (view, url) ->
            view.setOnClickListener { openLinkInBrowser(url) }
        }
    }

    private fun openLinkInBrowser(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        startActivity(intent)
    }

    private fun setAppVersion() {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        binding.version.text = packageInfo.versionName
    }
}
