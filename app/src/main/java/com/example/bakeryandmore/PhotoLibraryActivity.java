package com.example.bakeryandmore;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bakeryandmore.adapters.PhotoLibraryGridViewAdapter;
import com.example.bakeryandmore.data.FirebaseData;
import com.example.bakeryandmore.models.Category;
import com.example.bakeryandmore.models.Image;
import com.example.bakeryandmore.ui.LoadingDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.ArrayList;

public class PhotoLibraryActivity extends AppCompatActivity implements View.OnClickListener {

    /*-------- XML Element Variables --------*/
    private RecyclerView recyclerView;

    /*-------- Variables --------*/
    private Uri imageUri;
    private Category selectedCategory;
    private LoadingDialog loadingDialog;
    private PhotoLibraryGridViewAdapter photoLibraryGridViewAdapter;

    /*-------- Database Variables --------*/
    private FirebaseData firebaseData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_library);

        /*-------- Getting extras from previous Intent --------*/
        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
            selectedCategory = (Category) bundle.get("selectedCategory");

        /*-------- Init Variables --------*/
        loadingDialog = new LoadingDialog(PhotoLibraryActivity.this);
        firebaseData = new FirebaseData(getApplicationContext());

        /*-------- Hooks --------*/
        TextView toolbarCategoryNameTextView = findViewById(R.id.toolbar_category_name_textView);
        ImageButton slideshowToolbarImageButton = findViewById(R.id.slideshow_toolbar_imageButton);
        ImageButton deleteToolbarImageButton = findViewById(R.id.delete_toolbar_imageButton);
        ImageButton arrowBackToolbarImageButton = findViewById(R.id.arrow_back_photo_library_toolbar_imageButton);
        recyclerView = findViewById(R.id.recyclerView);
        FloatingActionButton fabAddImage = findViewById(R.id.fab_add_image);

        /*-------- Set category name text --------*/
        toolbarCategoryNameTextView.setText(selectedCategory.getName());

        setAdapter();

        /*-------- Event Listeners --------*/
        fabAddImage.setOnClickListener(this);
        arrowBackToolbarImageButton.setOnClickListener(this);
        slideshowToolbarImageButton.setOnClickListener(this);
        deleteToolbarImageButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fab_add_image) {
            loadingDialog.startLoadingDialog();
            pickFromGallery();
        } else if (view.getId() == R.id.arrow_back_photo_library_toolbar_imageButton) {
            finish();
        } else if (view.getId() == R.id.delete_toolbar_imageButton) {
            loadingDialog.startLoadingDialog();
            deleteImages();
        } else if (view.getId() == R.id.slideshow_toolbar_imageButton) {
            /*-------- It should be more than 1 selected images
                       from the user for then slideshow to start--------*/
            if (photoLibraryGridViewAdapter.getSelectedImagesItemCount() > 1) {
                Intent slideshowIntent = new Intent(PhotoLibraryActivity.this, SlideshowActivity.class);
                slideshowIntent.putExtra("slideshowImages", (Serializable) photoLibraryGridViewAdapter.getSelectedImagesList());
                startActivity(slideshowIntent);
            } else {
                Toast.makeText(this, "Παρακαλώ επιλέξτε περισσότερες εικόνες", Toast.LENGTH_SHORT).show();
            }

        }
    }

    /*-------- Call to firebase to delete selected imaged from firebase --------*/
    private void deleteImages() {
        firebaseData.deleteImages(selectedCategory, (ArrayList<Image>) photoLibraryGridViewAdapter.getSelectedImagesList());
        setAdapter();
        postDelayedExecute();
    }

    /*-------- Execute code after a delay --------*/
    private void postDelayedExecute() {
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> loadingDialog.dismissDialog(), 1000);
    }

    /*-------- Open device's gallery --------*/
    private void pickFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            saveImage();
        } else
            loadingDialog.dismissDialog();
    }

    /*-------- Save the selected image on Firebase Cloud Storage and
               then the whole Image object on Firebase Cloud Firestore --------*/
    private void saveImage() {
        firebaseData.uploadImage(selectedCategory.getName(), imageUri, true, (url, imageName) -> {
            firebaseData.updateCategoryImages(selectedCategory, imageName, url);
            setAdapter();
            postDelayedExecute();
        });

    }

    /*-------- Set PhotoLibraryGridViewAdapter --------*/
    public void setAdapter() {
        photoLibraryGridViewAdapter = new PhotoLibraryGridViewAdapter(getApplicationContext(), selectedCategory.getImages(), PhotoLibraryActivity.this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 5, GridLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(photoLibraryGridViewAdapter);
        photoLibraryGridViewAdapter.setActivityLayout();
    }

    @Override
    public void onBackPressed() {
        /*-------- In case there are selected images and the user press
                   device's back arrow then unselect all the selected
                   images and call the method setAdapter() to set the adapter --------*/
        if (photoLibraryGridViewAdapter != null && photoLibraryGridViewAdapter.getSelectedImagesItemCount() > 0) {
            photoLibraryGridViewAdapter.setSelectedImagesList(new ArrayList<>());

            setAdapter();
        } else {
            super.onBackPressed();
        }

    }

}