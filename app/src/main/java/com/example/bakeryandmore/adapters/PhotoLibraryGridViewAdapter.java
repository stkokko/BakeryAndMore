package com.example.bakeryandmore.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.bakeryandmore.R;
import com.example.bakeryandmore.models.Image;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PhotoLibraryGridViewAdapter extends RecyclerView.Adapter<PhotoLibraryGridViewAdapter.ViewHolder> {

    /*-------- XML Element Variables --------*/
    LinearLayout toolbarActions;
    TextView toolbarCategoryNameTextView;
    TextView toolbarSelectedImageTextView;
    FloatingActionButton fabAddImage;

    /*-------- Variables --------*/
    RequestOptions options;
    Context context;
    Activity activityPhotoLibrary;
    List<Image> imageList;
    List<Image> selectedImagesList;

    /*-------- Constructor --------*/
    public PhotoLibraryGridViewAdapter(Context context, List<Image> imageList, Activity activityPhotoLibrary) {
        this.context = context;
        this.imageList = imageList;
        this.activityPhotoLibrary = activityPhotoLibrary;

        /*-------- Init Variables --------*/
        options = new RequestOptions().centerCrop().placeholder(R.drawable.progress_animation);
        selectedImagesList = new ArrayList<>();

        /*-------- Hooks --------*/
        toolbarActions = activityPhotoLibrary.findViewById(R.id.toolbar_actions);
        toolbarCategoryNameTextView = activityPhotoLibrary.findViewById(R.id.toolbar_category_name_textView);
        toolbarSelectedImageTextView = activityPhotoLibrary.findViewById(R.id.toolbar_selected_images_textView);
        fabAddImage = activityPhotoLibrary.findViewById(R.id.fab_add_image);
    }


    @NonNull
    @Override
    public PhotoLibraryGridViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoLibraryGridViewAdapter.ViewHolder holder, int position) {
        Image image = imageList.get(position);
        Glide.with(context).load(image.getImageURL()).apply(options).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public List<Image> getSelectedImagesList() {
        return selectedImagesList;
    }

    public void setSelectedImagesList(ArrayList<Image> selectedImagesList) {
        this.selectedImagesList = selectedImagesList;
    }

    public int getSelectedImagesItemCount() {
        return selectedImagesList.size();
    }

    /*-------- set PhotoLibraryActivity layout depending
               on how many images has been selected --------*/
    @SuppressLint("SetTextI18n")
    public void setActivityLayout() {
        if (getSelectedImagesItemCount() == 0) {
            toolbarCategoryNameTextView.setVisibility(View.VISIBLE);
            toolbarSelectedImageTextView.setText("");
            toolbarSelectedImageTextView.setVisibility(View.GONE);
            toolbarActions.setVisibility(View.GONE);
            fabAddImage.setVisibility(View.VISIBLE);
        } else {
            toolbarCategoryNameTextView.setVisibility(View.GONE);
            toolbarSelectedImageTextView.setText("Επιλεγμένα   " + getSelectedImagesItemCount());
            toolbarSelectedImageTextView.setVisibility(View.VISIBLE);
            toolbarActions.setVisibility(View.VISIBLE);
            fabAddImage.setVisibility(View.GONE);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {

        /*-------- XML Element Variables --------*/
        LinearLayout itemLayout;
        CardView cardView;
        CheckBox checkbox;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            /*-------- Hooks --------*/
            cardView = itemView.findViewById(R.id.grid_cardView);
            checkbox = itemView.findViewById(R.id.grid_checkbox);
            imageView = itemView.findViewById(R.id.grid_imageView);
            itemLayout = itemView.findViewById(R.id.grid_item_layout);

            /*-------- Event Listeners --------*/
            cardView.setOnLongClickListener(this);
            cardView.setOnClickListener(this);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public boolean onLongClick(View view) {

            if (view.getId() == R.id.grid_cardView) {

                if (imageList != null && imageList.size() > 0) {

                    if (!checkbox.isChecked()) {
                        checkbox.setChecked(true);
                        checkbox.setVisibility(View.VISIBLE);
                        selectedImagesList.add(imageList.get(getAdapterPosition()));

                        setActivityLayout();
                        itemLayout.setAlpha(0.5F);
                    }

                }

            }

            return true;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onClick(View view) {

            if (view.getId() == R.id.grid_cardView) {

                if (imageList != null && imageList.size() > 0) {
                    if (selectedImagesList.size() > 0 && checkbox.isChecked()) {
                        checkbox.setChecked(false);
                        checkbox.setVisibility(View.GONE);
                        itemLayout.setAlpha(1);
                        selectedImagesList.remove(imageList.get(getAdapterPosition()));

                        toolbarCategoryNameTextView.setVisibility(View.GONE);
                        toolbarSelectedImageTextView.setText("Επιλεγμένα   " + getSelectedImagesItemCount());

                        setActivityLayout();
                    } else {

                        /*-------- if there are not selected images then make a list
                                   of all images having as first the one the user clicked
                                   on, and then pass the list on the ImageSliderActivity --------*/
                        if (getSelectedImagesItemCount() <= 0) {
                            ArrayList<Image> imageSliderList = new ArrayList<>();

                            if (getAdapterPosition() == 0) imageSliderList.addAll(imageList);
                            else if (getAdapterPosition() == imageList.size() - 1) {
                                imageSliderList.add(imageList.get(getItemCount() - 1));
                                imageSliderList.addAll(imageList);
                                imageSliderList.remove(getItemCount());
                            } else {
                                ArrayList<Image> tempImageList = new ArrayList<>();

                                for (int i = 0; i < getAdapterPosition(); i++)
                                    tempImageList.add(imageList.get(i));

                                for (int i = getAdapterPosition(); i < imageList.size(); i++)
                                    imageSliderList.add(imageList.get(i));

                                imageSliderList.addAll(tempImageList);

                            }

                            Intent imageSliderIntent = new Intent(context, com.example.bakeryandmore.ImageSliderActivity.class);
                            imageSliderIntent.putExtra("imageSliderData", (Serializable) imageSliderList);
                            activityPhotoLibrary.startActivity(imageSliderIntent);
                            activityPhotoLibrary.overridePendingTransition(R.anim.zoom_in, R.anim.static_animation);
                        }

                    }
                }

            }

        }
    }

}
