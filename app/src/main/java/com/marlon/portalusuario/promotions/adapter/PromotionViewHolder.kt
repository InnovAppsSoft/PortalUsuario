package com.marlon.portalusuario.promotions.adapter

import android.content.Intent
import android.graphics.drawable.PictureDrawable
import android.net.Uri
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.github.twocoffeesoneteam.glidetovectoryou.GlideApp
import com.marlon.portalusuario.databinding.PromoSliderLayoutBinding
import com.marlon.portalusuario.promotions.model.Promotion
import com.marlon.portalusuario.promotions.svg.SvgSoftwareLayerSetter
import com.smarteist.autoimageslider.SliderViewAdapter

class PromotionViewHolder(itemView: View) : SliderViewAdapter.ViewHolder(itemView) {
    private var binding = PromoSliderLayoutBinding.bind(itemView)

    fun render(promotion: Promotion) {
        // from url in your imageview.
        Glide.with(binding.ivPromotionBackground.context)
            .load(promotion.background)
            .into(binding.ivPromotionBackground)

        val requestBuilder = GlideApp.with(binding.ivPromotionSvg.context)
            .`as`(PictureDrawable::class.java)
            .transition(withCrossFade())
            .listener(SvgSoftwareLayerSetter())
        requestBuilder.load(Uri.parse(promotion.svg))
            .into(binding.ivPromotionSvg)

        itemView.setOnClickListener {
            it.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(promotion.url)))
        }
    }
}
