package com.example.bakeryandmore;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.bakeryandmore.data.FirebaseData;
import com.example.bakeryandmore.data.FirebaseDataCategoriesAsyncResponse;
import com.example.bakeryandmore.models.Category;
import com.example.bakeryandmore.ui.LoadingDialog;
import com.google.android.material.textfield.TextInputEditText;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;

public class EditCategoryActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    /*-------- XML Element Variables --------*/
    private TextInputEditText categoryNameEditText;
    private ImageView previewImageView;
    private AppCompatButton editCategoryButton;

    /*-------- Variables --------*/
    private Uri selectedImageUri;
    private LoadingDialog loadingDialog;
    private Category selectedCategory;
    private boolean categoryImageChanged = false;

    /*-------- Database Variables --------*/
    private FirebaseData firebaseData;

    ActivityResultLauncher<String> mTakePhoto;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category);

        /*-------- Getting extras from previous Intent --------*/
        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
            selectedCategory = (Category) bundle.get("selectedCategory");

        /*-------- Init Variables --------*/
        firebaseData = new FirebaseData(getApplicationContext());
        loadingDialog = new LoadingDialog(this);
        RequestOptions options = new RequestOptions().centerCrop().placeholder(R.drawable.progress_animation);

        /*-------- Hooks --------*/
        ImageButton arrowBackToolbarImageButton = findViewById(R.id.arrow_back_edit_category_toolbar_imageButton);
        TextView editCategoryTextView = findViewById(R.id.edit_category_textView);
        categoryNameEditText = findViewById(R.id.edit_category_categoryName_editText);
        previewImageView = findViewById(R.id.edit_category_preview_imageView);
        ImageView editImageImageView = findViewById(R.id.edit_category_edit_image_icon_imageView);
        editCategoryButton = findViewById(R.id.edit_category_button);

        /*-------- Set the views --------*/
        editCategoryTextView.setText("Επεξεργασία κατηγορίας " + selectedCategory.getName());
        categoryNameEditText.setText(selectedCategory.getName());
        editImageImageView.setAlpha(0f);
        Glide.with(getApplicationContext()).load(selectedCategory.getCategoryImage().getImageURL()).apply(options).into(previewImageView);

        /*-------- Event Listeners --------*/
        arrowBackToolbarImageButton.setOnClickListener(this);
        editImageImageView.setOnClickListener(this);
        editCategoryButton.setOnClickListener(this);
        categoryNameEditText.addTextChangedListener(this);

        mTakePhoto = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            selectedImageUri = result;
            try {
                InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                previewImageView.setImageBitmap(bitmap);
                categoryImageChanged = true;
            } catch (Exception e) {
                new RequestOptions().centerCrop().placeholder(R.drawable.progress_animation);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.arrow_back_edit_category_toolbar_imageButton) {
            finish();
        } else if (v.getId() == R.id.edit_category_edit_image_icon_imageView) {
            mTakePhoto.launch("image/*");
        } else if (v.getId() == R.id.edit_category_button) {
            loadingDialog.startLoadingDialog();

            if (!selectedCategory.getName().trim().equals(Objects.requireNonNull(categoryNameEditText.getText()).toString().trim()) && selectedImageUri != null)
                updateBothCategoryNameAndImage();
            else if (!selectedCategory.getName().equals(Objects.requireNonNull(categoryNameEditText.getText()).toString().trim()))
                updateCategoryName();
            else if (selectedImageUri != null)
                updateCategoryImage();
        }
    }

    /*-------- Update category image --------*/
    private void updateCategoryImage() {
        firebaseData.uploadImage(selectedCategory, Objects.requireNonNull(categoryNameEditText.getText()).toString().trim(), selectedImageUri, new FirebaseDataCategoriesAsyncResponse() {

            @Override
            public void processGetCategoriesListFinished(ArrayList<Category> categories) {

            }

            @Override
            public void processUpdateCategoryImageFinished(String url, String imageName) {
                firebaseData.updateCategory(selectedCategory, url, imageName);
                postDelayedExecute();
            }
        });
    }

    /*-------- Update category name --------*/
    private void updateCategoryName() {
        firebaseData.updateCategory(selectedCategory, Objects.requireNonNull(categoryNameEditText.getText()).toString().trim());
        postDelayedExecute();
    }

    /*-------- Update both category's name and image --------*/
    private void updateBothCategoryNameAndImage() {
        firebaseData.uploadImage(selectedCategory, Objects.requireNonNull(categoryNameEditText.getText()).toString().trim(), selectedImageUri, new FirebaseDataCategoriesAsyncResponse() {

            @Override
            public void processGetCategoriesListFinished(ArrayList<Category> categories) {

            }

            @Override
            public void processUpdateCategoryImageFinished(String url, String imageName) {
                firebaseData.updateCategory(selectedCategory, categoryNameEditText.getText().toString().trim(), url, imageName);
                postDelayedExecute();
            }
        });
    }

    /*-------- Execute code after a delay --------*/
    private void postDelayedExecute() {
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            loadingDialog.dismissDialog();
            finish();
        }, 2000);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (selectedImageUri != null) {
            editCategoryButton.setEnabled(true);
            editCategoryButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.button_next_bg));
        } else {
            editCategoryButton.setEnabled(false);
            editCategoryButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.button_next_bg_with_opacity));
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.toString().trim().length() > 0) {
            if (!selectedCategory.getName().trim().equals(Objects.requireNonNull(categoryNameEditText.getText()).toString().trim())) {
                editCategoryButton.setEnabled(true);
                editCategoryButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.button_next_bg));
            } else {
                if (categoryImageChanged) {
                    editCategoryButton.setEnabled(true);
                    editCategoryButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.button_next_bg));
                    categoryImageChanged = true;
                } else {
                    editCategoryButton.setEnabled(false);
                    editCategoryButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.button_next_bg_with_opacity));
                    categoryImageChanged = false;
                }
            }
        } else {
            editCategoryButton.setEnabled(false);
            editCategoryButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.button_next_bg_with_opacity));
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}