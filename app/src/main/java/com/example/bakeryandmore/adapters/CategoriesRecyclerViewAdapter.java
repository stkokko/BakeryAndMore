package com.example.bakeryandmore.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.bakeryandmore.EditCategoryActivity;
import com.example.bakeryandmore.PhotoLibraryActivity;
import com.example.bakeryandmore.R;
import com.example.bakeryandmore.SlideshowActivity;
import com.example.bakeryandmore.data.FirebaseData;
import com.example.bakeryandmore.models.Category;
import com.example.bakeryandmore.models.Image;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class CategoriesRecyclerViewAdapter extends RecyclerView.Adapter<CategoriesRecyclerViewAdapter.ViewHolder> {

    /*-------- Variables --------*/
    Context context;
    ArrayList<Category> categories;
    Activity activityMain;
    RequestOptions options;
    int selectedCategoryPos = -1;

    /*-------- Constructor --------*/
    public CategoriesRecyclerViewAdapter(Context context, ArrayList<Category> categories, Activity activityMain) {
        this.context = context;
        this.categories = categories;
        this.activityMain = activityMain;

        /*-------- Init Variables --------*/
        options = new RequestOptions().centerCrop().placeholder(R.drawable.progress_animation);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.main_categories_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (selectedCategoryPos != position)
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white));
        else
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.app_backgroundColor));
        Image image = categories.get(position).getCategoryImage();
        Glide.with(context).load(image.getImageURL()).apply(options).into(holder.roundedImageView);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        /*-------- XML Element Variables --------*/
        CardView cardView;
        RoundedImageView roundedImageView;

        TextView selectCategoryTextView;
        ImageButton photoLibraryImageButton;
        ImageButton slideshowImageButton;
        ImageButton editCategoryImageButton;
        ImageButton deleteCategoryImageButton;
        AppCompatButton nextButton;
        LinearLayout mainBottomActionBar;
        RecyclerView mainCategoriesRecyclerView;

        /*-------- Variables --------*/
        boolean photoLibrarySelected = false;
        boolean slideshowSelected = false;
        boolean editCategorySelected = false;
        boolean deleteCategorySelected = false;

        FirebaseData firebaseData;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            /*-------- Init Variables --------*/
            firebaseData = new FirebaseData(context);

            /*-------- Hooks --------*/
            cardView = itemView.findViewById(R.id.main_categories_item_cardView);
            roundedImageView = itemView.findViewById(R.id.main_categories_item_roundedImageView);

            selectCategoryTextView = activityMain.findViewById(R.id.select_category);
            photoLibraryImageButton = activityMain.findViewById(R.id.photo_library_imageButton);
            slideshowImageButton = activityMain.findViewById(R.id.slideshow_imageButton);
            editCategoryImageButton = activityMain.findViewById(R.id.edit_category_imageButton);
            deleteCategoryImageButton = activityMain.findViewById(R.id.delete_category_imageButton);
            nextButton = activityMain.findViewById(R.id.next_button);
            mainBottomActionBar = activityMain.findViewById(R.id.main_bottom_action_bar);
            mainCategoriesRecyclerView = activityMain.findViewById(R.id.main_categories_recyclerView);

            /*-------- Event Listeners --------*/
            cardView.setOnClickListener(this);
            photoLibraryImageButton.setOnClickListener(this);
            slideshowImageButton.setOnClickListener(this);
            editCategoryImageButton.setOnClickListener(this);
            deleteCategoryImageButton.setOnClickListener(this);
            nextButton.setOnClickListener(this);
        }

        /*-------- show bottom action bar for categories --------*/
        public void showBottomActionBar(boolean show) {
            if (show) {
                mainBottomActionBar.setVisibility(View.VISIBLE);
                mainBottomActionBar.animate()
                        .translationY(-240)
                        .setStartDelay(200)
                        .setDuration(1000);
            } else {
                mainBottomActionBar.setVisibility(View.GONE);
                mainBottomActionBar.animate()
                        .translationY(240)
                        .setStartDelay(200)
                        .setDuration(1000);
            }
        }

        /*-------- set background of bottom action bar buttons --------*/
        public void setBottomActionBarButtons(int buttonId) {
            if (buttonId == R.id.photo_library_imageButton) {
                photoLibraryImageButton.setBackground(ContextCompat.getDrawable(context, R.drawable.bottom_action_icon));
                slideshowImageButton.setBackground(ContextCompat.getDrawable(context, R.drawable.bottom_action_bar_bg));
                editCategoryImageButton.setBackground(ContextCompat.getDrawable(context, R.drawable.bottom_action_bar_bg));
                deleteCategoryImageButton.setBackground(ContextCompat.getDrawable(context, R.drawable.bottom_action_bar_bg));

                photoLibrarySelected = true;
                slideshowSelected = false;
                editCategorySelected = false;
                deleteCategorySelected = false;
            } else if (buttonId == R.id.slideshow_imageButton) {
                photoLibraryImageButton.setBackground(ContextCompat.getDrawable(context, R.drawable.bottom_action_bar_bg));
                slideshowImageButton.setBackground(ContextCompat.getDrawable(context, R.drawable.bottom_action_icon));
                editCategoryImageButton.setBackground(ContextCompat.getDrawable(context, R.drawable.bottom_action_bar_bg));
                deleteCategoryImageButton.setBackground(ContextCompat.getDrawable(context, R.drawable.bottom_action_bar_bg));

                photoLibrarySelected = false;
                slideshowSelected = true;
                editCategorySelected = false;
                deleteCategorySelected = false;
            } else if (buttonId == R.id.edit_category_imageButton) {
                photoLibraryImageButton.setBackground(ContextCompat.getDrawable(context, R.drawable.bottom_action_bar_bg));
                slideshowImageButton.setBackground(ContextCompat.getDrawable(context, R.drawable.bottom_action_bar_bg));
                editCategoryImageButton.setBackground(ContextCompat.getDrawable(context, R.drawable.bottom_action_icon));
                deleteCategoryImageButton.setBackground(ContextCompat.getDrawable(context, R.drawable.bottom_action_bar_bg));

                photoLibrarySelected = false;
                slideshowSelected = false;
                editCategorySelected = true;
                deleteCategorySelected = false;
            } else if (buttonId == R.id.delete_category_imageButton) {
                photoLibraryImageButton.setBackground(ContextCompat.getDrawable(context, R.drawable.bottom_action_bar_bg));
                slideshowImageButton.setBackground(ContextCompat.getDrawable(context, R.drawable.bottom_action_bar_bg));
                editCategoryImageButton.setBackground(ContextCompat.getDrawable(context, R.drawable.bottom_action_bar_bg));
                deleteCategoryImageButton.setBackground(ContextCompat.getDrawable(context, R.drawable.bottom_action_icon));

                photoLibrarySelected = false;
                slideshowSelected = false;
                editCategorySelected = false;
                deleteCategorySelected = true;
            } else {
                photoLibraryImageButton.setBackground(ContextCompat.getDrawable(context, R.drawable.bottom_action_bar_bg));
                slideshowImageButton.setBackground(ContextCompat.getDrawable(context, R.drawable.bottom_action_bar_bg));
                editCategoryImageButton.setBackground(ContextCompat.getDrawable(context, R.drawable.bottom_action_bar_bg));
                deleteCategoryImageButton.setBackground(ContextCompat.getDrawable(context, R.drawable.bottom_action_bar_bg));
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.main_categories_item_cardView) {

                if (selectedCategoryPos != getAdapterPosition()) {
                    notifyItemChanged(selectedCategoryPos);
                    setBottomActionBarButtons(-1);
                    nextButton.setBackground(ContextCompat.getDrawable(context, R.drawable.button_next_bg_with_opacity));

                    if (selectedCategoryPos < 0) showBottomActionBar(true);

                    selectedCategoryPos = getAdapterPosition();
                    cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.app_backgroundColor));
                    selectCategoryTextView.setText(categories.get(selectedCategoryPos).getName());
                }

            } else if (v.getId() == R.id.photo_library_imageButton || v.getId() == R.id.slideshow_imageButton || v.getId() == R.id.edit_category_imageButton || v.getId() == R.id.delete_category_imageButton) {

                if (v.getId() == R.id.slideshow_imageButton) {
                    if (categories.get(selectedCategoryPos).getImages().size() > 0) {
                        setBottomActionBarButtons(v.getId());
                        nextButton.setBackground(ContextCompat.getDrawable(context, R.drawable.button_next_bg));
                        nextButton.setEnabled(true);
                    } else {
                        Toast.makeText(context, "Δεν υπάρχουν διαθέσιμες εικόνες", Toast.LENGTH_SHORT).show();
                        nextButton.setEnabled(false);
                    }

                } else {
                    setBottomActionBarButtons(v.getId());
                    nextButton.setBackground(ContextCompat.getDrawable(context, R.drawable.button_next_bg));
                    nextButton.setEnabled(true);
                }

            } else if (v.getId() == R.id.next_button) {
                if (photoLibrarySelected || slideshowSelected || editCategorySelected || deleteCategorySelected) {

                    if (photoLibrarySelected) {
                        Intent photoLibraryIntent = new Intent(activityMain, PhotoLibraryActivity.class);
                        photoLibraryIntent.putExtra("selectedCategory", categories.get(selectedCategoryPos));
                        activityMain.startActivity(photoLibraryIntent);
                    } else if (slideshowSelected) {
                        Intent slideshowIntent = new Intent(activityMain, SlideshowActivity.class);
                        slideshowIntent.putExtra("slideshowImages", categories.get(selectedCategoryPos).getImages());
                        activityMain.startActivity(slideshowIntent);
                    } else if (editCategorySelected) {
                        Intent editCategoryIntent = new Intent(activityMain, EditCategoryActivity.class);
                        editCategoryIntent.putExtra("selectedCategory", categories.get(selectedCategoryPos));
                        activityMain.startActivity(editCategoryIntent);
                    } else {
                        AlertDialog alertDialog = new AlertDialog.Builder(activityMain).create();
                        alertDialog.setTitle("Διαγραφή");
                        alertDialog.setMessage("Διαγραφή κατηγορίας " + categories.get(selectedCategoryPos).getName().trim());
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "ΔΙΑΓΡΑΦΗ", (dialog, which) -> {
                            firebaseData.deleteCategory(categories.get(selectedCategoryPos));
                            alertDialog.dismiss();

                            categories.remove(selectedCategoryPos);
                            notifyDataSetChanged();
                            selectedCategoryPos = -1;
                            showBottomActionBar(false);
                            selectCategoryTextView.setText("Επιλέξτε κατηγορία");
                        });
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "ΑΚΥΡΩΣΗ", (dialog, which) -> alertDialog.dismiss());
                        alertDialog.show();
                    }
                }

            }

        }
    }
}

