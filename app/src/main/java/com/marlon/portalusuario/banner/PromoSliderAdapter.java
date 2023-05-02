package com.marlon.portalusuario.banner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.marlon.portalusuario.R;
import com.marlon.portalusuario.errores_log.JCLogging;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.marlon.portalusuario.banner.PromoSliderAdapter.SliderAdapterViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PromoSliderAdapter extends SliderViewAdapter<SliderAdapterViewHolder> {

    private final List<Banner> mSliderItems;
    private final Context context;
    private JCLogging Logging;

    // Constructor
    public PromoSliderAdapter(Context context, ArrayList<Banner> BannerArrayList) {
        this.mSliderItems = BannerArrayList;
        this.context = context;
        Logging = new JCLogging(context);
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
            final Banner sliderItem = mSliderItems.get(position);
            // mostrar la descripcion en el TextView
            viewHolder.description.setText(sliderItem.getTitle());
            viewHolder.description.setTextSize(10.0f);
            viewHolder.description.setTextColor(-1);
            // from url in your imageview.
            Picasso.get()
                    .load(sliderItem.getImage())
                    .fit().centerInside()
                    .into(viewHolder.imageViewBackground);
            // evento on click
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Logging.message("Opening PROMO URL::url=" + sliderItem.getLink(), null);
                    Uri url = Uri.parse(sliderItem.getLink());
                    Intent openUrl = new Intent(Intent.ACTION_VIEW, url);
                    context.startActivity(openUrl);
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
        TextView description;

        public SliderAdapterViewHolder(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider);
            description = itemView.findViewById(R.id.tv_auto_image_slider);
            this.itemView = itemView;
        }
    }

    @Override
    public String toString() {
        return "PromoSliderAdapter{" +
                "mSliderItems=" + mSliderItems +
                ", context=" + context +
                ", Logging=" + Logging +
                '}';
    }
}
