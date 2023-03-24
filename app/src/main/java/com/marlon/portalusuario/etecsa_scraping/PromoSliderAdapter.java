package com.marlon.portalusuario.etecsa_scraping;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYouListener;
import com.marlon.portalusuario.view.activities.MainActivity;
import com.marlon.portalusuario.R;
import com.marlon.portalusuario.logging.JCLogging;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.marlon.portalusuario.etecsa_scraping.PromoSliderAdapter.SliderAdapterViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PromoSliderAdapter extends SliderViewAdapter<SliderAdapterViewHolder> {

    private final List<Promo> mSliderItems;
    private final Context context;
    private JCLogging Logging;

    // Constructor
    public PromoSliderAdapter(Context context, ArrayList<Promo> promoArrayList) {
        this.mSliderItems = promoArrayList;
        this.context = context;
        Logging = new JCLogging(context);
        //
    }

    // We are inflating the slider_layout
    // inside on Create View Holder method.
    @Override
    public SliderAdapterViewHolder onCreateViewHolder(ViewGroup parent) {
        @SuppressLint("InflateParams") View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.promo_slider_layout, null);
        return new SliderAdapterViewHolder(inflate);
    }

    // Inside on bind view holder we will
    // set data to item of Slider View.
    @Override
    public void onBindViewHolder(SliderAdapterViewHolder viewHolder, final int position) {
        try {
            final Promo sliderItem = mSliderItems.get(position);
            // from url in your imageview.
            Picasso.get()
                    .load(sliderItem.getImage())
                    .fit().centerInside()
                    .into(viewHolder.imageViewBackground);
            Uri uri = Uri.parse(sliderItem.getSvg());//"https://www.etecsa.cu/sites/default/files/promocion/IMAGENTOPSLAIDER_BIENBENIDA2_ETECSA_4.svg"
            GlideToVectorYou
                    .init()
                    .with(context)
                    .withListener(new GlideToVectorYouListener() {
                        @Override
                        public void onLoadFailed() {
                            //Toast.makeText(context, "Load failed", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onResourceReady() {
                            //Toast.makeText(context, "Image ready", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .load(uri, viewHolder.imageViewSVG);
            // evento on click
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.openLink(sliderItem.getLink());
                }
            });
        }catch (Exception ex){
            ex.printStackTrace();
            Logging.error(null, null, ex);
        }
    }

    // this method will return
    // the count of our list.
    @Override
    public int getCount() {
        return mSliderItems.size();
    }

    static class SliderAdapterViewHolder extends SliderViewAdapter.ViewHolder {
        // Adapter class for initializing
        // the views of our slider view.
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
