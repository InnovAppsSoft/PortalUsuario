package com.marlon.portalusuario.promotions.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.marlon.portalusuario.R
import com.marlon.portalusuario.promotions.model.Promotion
import com.smarteist.autoimageslider.SliderViewAdapter

class PromoSliderAdapter(private val promotions: List<Promotion>) : SliderViewAdapter<PromotionViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup): PromotionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return PromotionViewHolder(inflater.inflate(R.layout.promo_slider_layout, parent, false))
    }

    override fun onBindViewHolder(viewHolder: PromotionViewHolder, position: Int) {
        viewHolder.render(promotions[position])
    }

    override fun getCount(): Int {
        return promotions.size
    }
}
