package com.example.bakeryandmore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.bakeryandmore.adapters.CategoriesRecyclerViewAdapter;
import com.example.bakeryandmore.data.FirebaseData;
import com.example.bakeryandmore.data.FirebaseDataCategoriesAsyncResponse;
import com.example.bakeryandmore.models.Category;
import com.example.bakeryandmore.ui.LoadingDialog;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    /*-------- XML Element Variables --------*/
    private RecyclerView mainCategoriesRecyclerView;
    private TextView selectCategoryTextView;
    private ImageButton photoLibraryImageButton;
    private ImageButton slideshowImageButton;
    private ImageButton editCategoryImageButton;
    private ImageButton deleteCategoryImageButton;
    private AppCompatButton nextButton;
    private LinearLayout mainBottomActionBar;

    /*-------- Variables --------*/
    private CategoriesRecyclerViewAdapter categoriesRecyclerViewAdapter;

    /*-------- Database Variables --------*/
    private FirebaseData firebaseData;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*-------- Init Variables --------*/
        firebaseData = new FirebaseData(this);
        loadingDialog = new LoadingDialog(MainActivity.this);

        /*-------- Hooks --------*/
        mainCategoriesRecyclerView = findViewById(R.id.main_categories_recyclerView);
        selectCategoryTextView = findViewById(R.id.select_category);
        RoundedImageView addCategoryRoundedImageView = findViewById(R.id.main_add_category);
        photoLibraryImageButton = findViewById(R.id.photo_library_imageButton);
        slideshowImageButton = findViewById(R.id.slideshow_imageButton);
        editCategoryImageButton = findViewById(R.id.edit_category_imageButton);
        deleteCategoryImageButton = findViewById(R.id.delete_category_imageButton);
        nextButton = findViewById(R.id.next_button);
        mainBottomActionBar = findViewById(R.id.main_bottom_action_bar);

        /*-------- Event Listeners --------*/
        addCategoryRoundedImageView.setOnClickListener(this);
    }

    /*-------- Getting the categories array list from firestore and then set the adapter --------*/
    public void getCategories() {
        firebaseData.getCategories(new FirebaseDataCategoriesAsyncResponse() {
                                       @Override
                                       public void processGetCategoriesListFinished(ArrayList<Category> categories) {
                                           categoriesRecyclerViewAdapter = new CategoriesRecyclerViewAdapter(getApplicationContext(), categories, MainActivity.this);

                                           mainCategoriesRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
                                           mainCategoriesRecyclerView.setHasFixedSize(true);
                                           mainCategoriesRecyclerView.setAdapter(categoriesRecyclerViewAdapter);

                                           loadingDialog.dismissDialog();
                                       }

                                       @Override
                                       public void processUpdateCategoryImageFinished(String url, String imageName) {

                                       }
                                   }

        );
    }

    /*-------- Set bottom action bar layout as default --------*/
    private void defaultLayout() {
        selectCategoryTextView.setText("Επιλέξτε κατηγορία");
        photoLibraryImageButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bottom_action_bar_bg));
        slideshowImageButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bottom_action_bar_bg));
        editCategoryImageButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bottom_action_bar_bg));
        deleteCategoryImageButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bottom_action_bar_bg));
        nextButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.button_next_bg_with_opacity));

        mainBottomActionBar.setVisibility(View.GONE);
        mainBottomActionBar.animate()
                .translationY(150)
                .setStartDelay(200)
                .setDuration(1000);
    }

    @Override
    protected void onRestart() {
        defaultLayout();
        super.onRestart();
    }

    @Override
    protected void onResume() {
        loadingDialog.startLoadingDialog();
        getCategories();
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.main_add_category) {
            Intent addCategoryIntent = new Intent(MainActivity.this, AddCategoryActivity.class);
            startActivity(addCategoryIntent);
        }
    }
}