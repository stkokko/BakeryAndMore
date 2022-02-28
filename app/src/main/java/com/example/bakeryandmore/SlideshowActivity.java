package com.example.bakeryandmore;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.bakeryandmore.adapters.SlideshowSliderViewAdapter;
import com.example.bakeryandmore.models.Image;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;

public class SlideshowActivity extends AppCompatActivity {

    /*-------- Variables --------*/
    private ArrayList<Image> slideshowImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slideshow);

        /*-------- Getting extras from previous Intent --------*/
        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
            slideshowImages = (ArrayList<Image>) bundle.getSerializable("slideshowImages");

        /*-------- Hooks --------*/
        SliderView sliderView = findViewById(R.id.sliderView);

        /*-------- Set SlideshowSliderViewAdapter --------*/
        SlideshowSliderViewAdapter slideshowSliderViewAdapter = new SlideshowSliderViewAdapter(getApplicationContext(), this, slideshowImages);
        sliderView.setSliderAdapter(slideshowSliderViewAdapter);
        sliderView.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
        sliderView.startAutoCycle();
    }
}