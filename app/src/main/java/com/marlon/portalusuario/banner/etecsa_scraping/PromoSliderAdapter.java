package com.marlon.portalusuario.banner.etecsa_scraping;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYouListener;
import com.marlon.portalusuario.R;
import com.marlon.portalusuario.activities.MainActivity;
import com.marlon.portalusuario.banner.etecsa_scraping.PromoSliderAdapter.SliderAdapterViewHolder;
import com.marlon.portalusuario.databinding.PromoSliderLayoutBinding;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.github.suitetecsa.sdk.promotion.model.Promotion;

public class PromoSliderAdapter extends SliderViewAdapter<SliderAdapterViewHolder> {
    private static final String TAG = "PromoSliderAdapter";
    private final List<Promotion> promotions;
    private final Context context;

    // Constructor
    public PromoSliderAdapter(Context context, List<Promotion> promotions) {
        this.context = context;
        this.promotions = promotions;
    }

    // We are inflating the slider_layout
    // inside on Create View Holder method.
    @Override
    public SliderAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent) {
        PromoSliderLayoutBinding binding = PromoSliderLayoutBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new SliderAdapterViewHolder(binding.getRoot());
    }

    // Inside on bind view holder we will
    // set data to item of Slider View.
    @Override
    public void onBindViewHolder(@NonNull SliderAdapterViewHolder viewHolder, final int position) {
        final Promotion promotion = promotions.get(position);
        // from url in your imageview.
        Picasso.get()
                .load(promotion.getJpgUrl())
                .fit().centerInside()
                .into(viewHolder.imageViewBackground);
        Uri uri = Uri.parse(promotion.getSvgUrl());
        Log.d(TAG, "onBindViewHolder: " + uri);
        GlideToVectorYou
                .init()
                .with(context)
                .withListener(new GlideToVectorYouListener() {
                    @Override
                    public void onLoadFailed() {
                        Log.e(TAG, "onLoadFailed: SVG load failed for url: " + uri);
                    }

                    @Override
                    public void onResourceReady() {
                        Log.d(TAG, "onResourceReady: SVG load success");
                    }
                })
                .load(uri, viewHolder.imageViewSVG);
        viewHolder.itemView.setOnClickListener(v -> MainActivity.openLink(promotion.getPromotionUrl()));
    }

    // this method will return
    // the count of our list.
    @Override
    public int getCount() {
        return promotions.size();
    }

    public static class SliderAdapterViewHolder extends SliderViewAdapter.ViewHolder {
        View itemView;
        ImageView imageViewBackground;
        ImageView imageViewSVG;

        public SliderAdapterViewHolder(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider);
            imageViewSVG = itemView.findViewById(R.id.iv_svg);
            this.itemView = itemView;
        }
    }
}
