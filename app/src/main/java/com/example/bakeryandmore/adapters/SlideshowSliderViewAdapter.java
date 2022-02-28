package com.example.bakeryandmore.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.bakeryandmore.R;
import com.example.bakeryandmore.models.Image;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.List;

public class SlideshowSliderViewAdapter extends SliderViewAdapter<SlideshowSliderViewAdapter.ViewHolder> {

    /*-------- Variables --------*/
    Context context;
    List<Image> imageList;
    RequestOptions options;
    Activity activitySlideshow;
    private long time;
    private int n = 0;

    /*-------- Constructor --------*/
    public SlideshowSliderViewAdapter(Context context, Activity activitySlideshow, List<Image> imageList) {
        this.context = context;
        this.activitySlideshow = activitySlideshow;
        this.imageList = imageList;

        /*-------- Init Variables --------*/
        options = new RequestOptions().centerCrop().placeholder(R.drawable.progress_animation);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.slider_view_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Image image = imageList.get(i);
        Glide.with(context).load(image.getImageURL()).apply(options).into(viewHolder.imageView);
    }

    @Override
    public int getCount() {
        return imageList.size();
    }


    public class ViewHolder extends SliderViewAdapter.ViewHolder implements View.OnClickListener {

        /*-------- XML Element Variables --------*/
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);

            /*-------- Hooks --------*/
            imageView = itemView.findViewById(R.id.sliderViewItem_imageView);

            /*-------- Event Listeners --------*/
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.sliderViewItem_imageView) { // On double-click in less than 800 millis finish the activity
                n++;
                if (n == 1) {
                    time = System.currentTimeMillis();
                } else if (n == 2) {
                    if (System.currentTimeMillis() - time > 800) n = 0;
                    else activitySlideshow.finish();
                }
            }
        }
    }

}
