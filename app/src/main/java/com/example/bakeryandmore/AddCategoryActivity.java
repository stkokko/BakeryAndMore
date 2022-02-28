package com.example.bakeryandmore;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.example.bakeryandmore.data.FirebaseData;
import com.example.bakeryandmore.ui.LoadingDialog;
import com.google.android.material.textfield.TextInputEditText;

import java.io.InputStream;
import java.util.Objects;

public class AddCategoryActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    /*-------- XML Element Variables --------*/
    private TextInputEditText categoryNameEditText;
    private ImageView previewImageView;
    private ImageView addImageImageView;
    private AppCompatButton saveCategoryButton;

    /*-------- Variables --------*/
    private Uri selectedImageUri;
    private LoadingDialog loadingDialog;
    private boolean categoryNameChanged = false;

    /*-------- Database Variables --------*/
    private FirebaseData firebaseData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        /*-------- Init Variables --------*/
        firebaseData = new FirebaseData(getApplicationContext());
        loadingDialog = new LoadingDialog(this);

        /*-------- Hooks --------*/
        ImageButton arrowBackToolbarImageButton = findViewById(R.id.arrow_back_add_category_toolbar_imageButton);
        categoryNameEditText = findViewById(R.id.add_category_categoryName_editText);
        previewImageView = findViewById(R.id.add_category_preview_imageView);
        addImageImageView = findViewById(R.id.add_category_add_image_icon_imageView);
        saveCategoryButton = findViewById(R.id.add_category_button);

        /*-------- Event Listeners --------*/
        arrowBackToolbarImageButton.setOnClickListener(this);
        addImageImageView.setOnClickListener(this);
        saveCategoryButton.setOnClickListener(this);
        categoryNameEditText.addTextChangedListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.arrow_back_add_category_toolbar_imageButton) {
            finish();
        } else if (view.getId() == R.id.add_category_add_image_icon_imageView) {
            loadingDialog.startLoadingDialog();
            pickFromGallery();
        } else if (view.getId() == R.id.add_category_button) {
            if (Objects.requireNonNull(categoryNameEditText.getText()).toString().trim().isEmpty()) {
                Toast.makeText(this, "Παρακαλώ συμπληρώστε το όνομα της κατηγορίας", Toast.LENGTH_SHORT).show();
            } else if (selectedImageUri == null) {
                Toast.makeText(this, "Παρακαλώ επιλέξτε εικόνα για την κατηγορία", Toast.LENGTH_SHORT).show();
            } else {
                loadingDialog.startLoadingDialog();
                saveCategory();
            }
        }
    }

    /*-------- Open device's gallery --------*/
    private void pickFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    /*-------- Uploading the selected image and then call a function
               to store the category object to firestore --------*/
    private void saveCategory() {
        firebaseData.uploadImage(Objects.requireNonNull(categoryNameEditText.getText()).toString().trim(), selectedImageUri, false, (url, imageName) -> {

            /*-------- Add new category to firestore --------*/
            firebaseData.addCategory(categoryNameEditText.getText().toString().trim(), imageName, url);

            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> {
                loadingDialog.dismissDialog();
                finish();
            }, 2000);
        });
    }

    @SuppressLint("CheckResult")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                previewImageView.setImageBitmap(bitmap);

                addImageImageView.setAlpha(0f);
            } catch (Exception e) {
                new RequestOptions().centerCrop().placeholder(R.drawable.progress_animation);
            }
        }
        loadingDialog.dismissDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (selectedImageUri != null && categoryNameChanged) {
            saveCategoryButton.setEnabled(true);
            saveCategoryButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.button_next_bg));
        } else {
            saveCategoryButton.setEnabled(false);
            saveCategoryButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.button_next_bg_with_opacity));
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.toString().trim().length() > 0) {
            if (selectedImageUri != null) {
                saveCategoryButton.setEnabled(true);
                saveCategoryButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.button_next_bg));
            }
            categoryNameChanged = true;
        } else {
            saveCategoryButton.setEnabled(false);
            saveCategoryButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.button_next_bg_with_opacity));
            categoryNameChanged = false;
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}