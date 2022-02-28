package com.example.bakeryandmore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.example.bakeryandmore.adapters.ImageSliderAdapter;
import com.example.bakeryandmore.models.Image;

import java.util.ArrayList;

public class ImageSliderActivity extends AppCompatActivity {

    /*-------- Variables --------*/
    private ArrayList<Image> imageSliderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_slider);

        /*-------- Getting extras from previous Intent --------*/
        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
            imageSliderList = (ArrayList<Image>) bundle.getSerializable("imageSliderData");

        /*-------- Hooks --------*/
        ViewPager2 imageSliderViewPager2 = findViewById(R.id.image_slider_ViewPager2);

        /*------------ Set View Pager's Adapter ------------*/
        imageSliderViewPager2.setAdapter(new ImageSliderAdapter(getApplicationContext(), this, imageSliderList));

        /*------------ Set Up View Pager ------------*/
        imageSliderViewPager2.setClipToPadding(false);
        imageSliderViewPager2.setClipChildren(false);
        imageSliderViewPager2.setOffscreenPageLimit(3);
        imageSliderViewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        /*------------ Set Up CompositePageTransformer ------------*/
        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.95f + r * 0.05f);
        });

        imageSliderViewPager2.setPageTransformer(compositePageTransformer);
    }
}