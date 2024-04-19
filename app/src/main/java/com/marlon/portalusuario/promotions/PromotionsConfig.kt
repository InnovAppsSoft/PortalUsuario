package com.marlon.portalusuario.promotions

import android.graphics.Color
import android.graphics.Paint
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.marlon.portalusuario.promotions.adapter.PromoSliderAdapter
import com.marlon.portalusuario.databinding.MainContentBinding
import com.marlon.portalusuario.promotions.model.Promotion
import com.marlon.portalusuario.util.Util
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView

private const val SCROLL_TIME = 4

class PromotionsConfig private constructor(
    private val activity: AppCompatActivity,
    private val viewModel: PromotionsViewModel,
    private val binding: MainContentBinding
) {
    fun setup(showPromotions: Boolean) {
        // setup try again textView
        binding.tryAgain.paintFlags = binding.tryAgain.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        binding.tryAgain.setOnClickListener { loadPromotions() }
        if (showPromotions) { loadPromotions() }
        // observe state
        viewModel.state.observe(activity) { state ->
            if (state.promotions.isEmpty() && !state.isLoading) {
                // hide progressBar and carousel
                binding.progressBar.visibility = View.INVISIBLE
                binding.imageSlider.visibility = View.INVISIBLE
                //show error
                binding.errorLayoutBanner.visibility = View.VISIBLE
            } else if (state.promotions.isNotEmpty()) {
                updatePromoSlider(state.promotions)
                // hide progressBar and error
                binding.progressBar.visibility = View.INVISIBLE
                binding.errorLayoutBanner.visibility = View.INVISIBLE
                //show carousel
                binding.imageSlider.visibility = View.VISIBLE
            }
            if (state.isLoading) {
                // show progressBar
                binding.progressBar.visibility = View.VISIBLE
                // hide carousel and error
                binding.imageSlider.visibility = View.INVISIBLE
                binding.errorLayoutBanner.visibility = View.INVISIBLE
            }
        }
        setCarouselVisibility(showPromotions)
    }

    private fun loadPromotions() {
        if (Util.isConnected(activity)) {
            viewModel.onEvent(PromotionEvent.Load)
        }
    }

    private fun updatePromoSlider(promotions: List<Promotion>) {
        if (promotions.isNotEmpty()) {
            val adapter = PromoSliderAdapter(promotions)
            binding.imageSlider.setSliderAdapter(adapter)
            // setting up el slider view
            binding.imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM)
            binding.imageSlider.setSliderTransformAnimation(SliderAnimations.CUBEINSCALINGTRANSFORMATION)
            binding.imageSlider.autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_RIGHT
            binding.imageSlider.indicatorSelectedColor = Color.WHITE
            binding.imageSlider.indicatorUnselectedColor = Color.GRAY
            binding.imageSlider.scrollTimeInSec = SCROLL_TIME
            binding.imageSlider.startAutoCycle()
        } else {
            binding.carouselLayout.visibility = View.GONE
        }
    }

    fun setCarouselVisibility(isVisible: Boolean) {
        binding.carouselLayout.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    class Builder {
        private lateinit var activity: AppCompatActivity
        private lateinit var viewModel: PromotionsViewModel
        private lateinit var binding: MainContentBinding

        fun activity(activity: AppCompatActivity) = apply { this.activity = activity }

        fun viewModel(viewModel: PromotionsViewModel) = apply { this.viewModel = viewModel }

        fun binding(binding: MainContentBinding) = apply { this.binding = binding }

        fun build() = PromotionsConfig(activity, viewModel, binding)
    }
}