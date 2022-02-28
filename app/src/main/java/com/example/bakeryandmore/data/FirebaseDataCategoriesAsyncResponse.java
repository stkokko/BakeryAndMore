package com.example.bakeryandmore.data;

import com.example.bakeryandmore.models.Category;

import java.util.ArrayList;

public interface FirebaseDataCategoriesAsyncResponse {

    void processGetCategoriesListFinished(ArrayList<Category> categories);

    void processUpdateCategoryImageFinished(String url, String imageName);
}
