package com.marlon.portalusuario.etecsa_scraping;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.marlon.portalusuario.R;
import com.marlon.portalusuario.logging.JCLogging;
import com.marlon.portalusuario.etecsa_scraping.NewsSliderAdapter.SliderAdapterViewHolder;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NewsSliderAdapter extends SliderViewAdapter<SliderAdapterViewHolder> {

    private final List<News> mSliderItems;
    private final Context context;
    // LOGGING
    private JCLogging Logging;

    // Constructor
    public NewsSliderAdapter(Context context, ArrayList<News> BannerArrayList) {
        this.mSliderItems = BannerArrayList;
        this.context = context;
        Logging = new JCLogging(context);
    }

    // We are inflating the slider_layout
    // inside on Create View Holder method.
    @Override
    public SliderAdapterViewHolder onCreateViewHolder(ViewGroup parent) {
        @SuppressLint("InflateParams") View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_slider_layout, null);
        return new SliderAdapterViewHolder(inflate);
    }

    // Inside on bind view holder we will
    // set data to item of Slider View.
    @Override
    public void onBindViewHolder(SliderAdapterViewHolder viewHolder, final int position) {
        try{
            final News sliderItem = mSliderItems.get(position);
            // mostrar la descripcion en el TextView
            viewHolder.description.setText(sliderItem.getTitle());
            viewHolder.description.setTextSize(10.0f);
            viewHolder.description.setTextColor(-1);
            // from url in your imageview.
            Random random = new Random();
            int r = random.nextInt(101);
            int g = random.nextInt(101);
            int b = random.nextInt(101);
            viewHolder.news_main_layout.setBackgroundColor(Color.rgb(r, g, b));
            // evento on click
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Logging.message("Opening news URL::url=" + sliderItem.getLink(), null);
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
        ImageView imageGifContainer;
        TextView description;
        FrameLayout news_main_layout;
        ImageView iv_news;

        public SliderAdapterViewHolder(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider);
            imageGifContainer = itemView.findViewById(R.id.iv_gif_container);
            description = itemView.findViewById(R.id.tv_auto_image_slider);
            iv_news = itemView.findViewById(R.id.iv_news);
            news_main_layout = itemView.findViewById(R.id.news_main_layout);
            this.itemView = itemView;
        }
    }
}
