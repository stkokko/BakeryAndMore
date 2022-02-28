package com.example.bakeryandmore.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.bakeryandmore.R;
import com.example.bakeryandmore.models.Image;

import java.util.List;

public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.ViewHolder> {

    /*-------- Variables --------*/
    List<Image> sliderImages;
    Context context;
    Activity activityImageSlider;
    RequestOptions options;
    private long time;
    private int n = 0;

    /*-------- Constructor --------*/
    public ImageSliderAdapter(Context context, Activity activityImageSlider, List<Image> sliderImages) {
        this.context = context;
        this.activityImageSlider = activityImageSlider;
        this.sliderImages = sliderImages;

        /*-------- Init Variables --------*/
        options = new RequestOptions().centerCrop().placeholder(R.drawable.progress_animation);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_slider_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Image image = sliderImages.get(position);
        Glide.with(context).load(image.getImageURL()).apply(options).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return sliderImages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        /*-------- XML Element Variables --------*/
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            /*-------- Hooks --------*/
            imageView = itemView.findViewById(R.id.image_slider_item_imageView);

            /*-------- Event Listeners --------*/
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.image_slider_item_imageView) { // On double-click in less than 800 millis finish the activity
                n++;
                if (n == 1) {
                    time = System.currentTimeMillis();
                } else if (n == 2) {
                    if (System.currentTimeMillis() - time > 800) n = 0;
                    else activityImageSlider.finish();
                }
            }
        }
    }

}
