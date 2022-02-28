package com.example.bakeryandmore.data;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import com.example.bakeryandmore.models.Category;
import com.example.bakeryandmore.models.Image;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FirebaseData {

    /*-------- Variables --------*/
    private final FirebaseFirestore firebaseFirestore;
    private final FirebaseStorage firebaseStorage;
    private final Context context;

    /*-------- Constructor --------*/
    public FirebaseData(Context context) {
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        this.context = context;
    }

    /*----- Update category's image list in firestore -----*/
    public void updateCategoryImages(Category category, String id, String imageURL) {
        Image image = new Image();
        image.setId(id);
        image.setImageURL(imageURL);

        category.getImages().add(image);

        firebaseFirestore.collection("Categories")
                .document(category.getName())
                .update("images", category.getImages())
                .addOnSuccessListener(unused -> Toast.makeText(context, "Η εικόνα αποθηκεύτηκε επιτυχώς", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, "Αποτυχία αποθήκευσης εικόνας", Toast.LENGTH_SHORT).show());
    }

    /*-------- Uploading the selected (for category's photo library) image --------*/
    public void uploadImage(String categoryName, Uri imageUri, boolean flag, final FirebaseDataImagesAsyncResponse callback) {

        /*----- Variables -----*/
        String imageName = categoryName + System.currentTimeMillis();
        StorageReference storageReference = firebaseStorage.getReference().child("images/" + imageName);

        /*----- Uploading Image To Firebase Storage -----*/
        storageReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl()
                        .addOnSuccessListener(uri -> {

                            if (flag)
                                Toast.makeText(context, "Η εικόνα αποθηκεύτηκε επιτυχώς", Toast.LENGTH_SHORT).show();

                            if (callback != null)
                                callback.processSaveImageFinished(uri.toString(), imageName);
                        }).addOnFailureListener(e -> Toast.makeText(context, "Αποτυχία αποθήκευσης εικόνας", Toast.LENGTH_SHORT).show()))
                .addOnFailureListener(exception -> Toast.makeText(context, "Αποτυχία αποθήκευσης εικόνας", Toast.LENGTH_SHORT).show())
                .addOnProgressListener(snapshot -> {

                });

    }

    /*-------- Delete category's images, that user has selected from category's
               photo library, from firestore and if it succeed do the same to storage too. --------*/
    public void deleteImages(Category category, ArrayList<Image> imagesToDelete) {

        ArrayList<Image> newCategoryImages = category.getImages();

        for (Image imageToDelete : imagesToDelete)
            for (Image image : newCategoryImages)
                if (image.getId().equals(imageToDelete.getId())) {
                    category.getImages().remove(image);
                    break;
                }

        firebaseFirestore.collection("Categories")
                .document(category.getName())
                .update("images", category.getImages())
                .addOnSuccessListener(unused -> {

                    /*-------- Deleting all selected images from storage --------*/
                    for (Image imageToDelete : imagesToDelete) {
                        StorageReference storageReference = firebaseStorage.getReference().child("images/" + imageToDelete.getId());

                        storageReference.delete()
                                .addOnSuccessListener(unused1 -> {

                                })
                                .addOnFailureListener(e -> {

                                });
                    }

                    if (imagesToDelete.size() == 1)
                        Toast.makeText(context, "Η εικόνα διαγράφηκε με επιτυχία", Toast.LENGTH_SHORT).show();
                    else {
                        Toast.makeText(context, "Οι εικόνες διαγράφηκαν με επιτυχία", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    if (imagesToDelete.size() == 1)
                        Toast.makeText(context, "Αποτυχία διαγραφής εικόνας", Toast.LENGTH_SHORT).show();
                    else {
                        Toast.makeText(context, "Αποτυχία διαγραφής εικόνων", Toast.LENGTH_SHORT).show();
                    }
                });


    }

    /*-------- Get all categories from firestore and store them into an array list --------*/
    public void getCategories(final FirebaseDataCategoriesAsyncResponse callback) {
        ArrayList<Category> categories = new ArrayList<>();

        firebaseFirestore.collection("Categories")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        Category category;
                        Image categoryImage;

                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                            HashMap<String, Object> categoryImageMap = (HashMap<String, Object>) documentSnapshot.getData().get("categoryImage");
                            ArrayList<Image> images = (ArrayList<Image>) documentSnapshot.getData().get("images");

                            category = new Category();
                            category.setName(String.valueOf(documentSnapshot.getData().get("name")));

                            categoryImage = new Image();
                            if (categoryImageMap != null) {
                                categoryImage.setId(String.valueOf(categoryImageMap.get("id")));
                                categoryImage.setImageURL(String.valueOf(categoryImageMap.get("imageURL")));
                            }

                            category.setCategoryImage(categoryImage);

                            if (images != null) {
                                if (images.size() == 0)
                                    category.setImages(new ArrayList<>());
                                else {
                                    ArrayList<Image> imageArrayList = new ArrayList<>();
                                    for (int i = 0; i < images.size(); i++) {
                                        Map<String, Object> imageMap = (Map<String, Object>) images.get(i);
                                        Image image = new Image();
                                        image.setId(String.valueOf(imageMap.get("id")));
                                        image.setImageURL(String.valueOf(imageMap.get("imageURL")));
                                        imageArrayList.add(image);
                                        category.setImages(imageArrayList);
                                    }
                                }
                            }

                            categories.add(category);
                        }

                        if (callback != null) callback.processGetCategoriesListFinished(categories);

                    } else {
                        Toast.makeText(context, "Αποτυχία φόρτωσης δεδομένων", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /*-------- Add category to firestore --------*/
    public void addCategory(String categoryName, String id, String imageURL) {
        Map<String, Object> categoryMap = new HashMap<>();
        categoryMap.put("name", categoryName);
        categoryMap.put("categoryImage", new Image(id, imageURL));
        categoryMap.put("images", new ArrayList<>());

        firebaseFirestore.collection("Categories")
                .document(String.valueOf(categoryMap.get("name")))
                .set(categoryMap)
                .addOnSuccessListener(unused -> Toast.makeText(context, "Η κατηγορία αποθηκεύτηκε επιτυχώς", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, "Αποτυχία αποθήκευσης κατηγορίας", Toast.LENGTH_SHORT).show());
    }

    /*-------- Update selected category's name --------*/
    public void updateCategory(Category category, String newCategoryName) {

        firebaseFirestore.collection("Categories")
                .document(category.getName())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Map<String, Object> categoryMap = new HashMap<>();

                        Map<String, Object> categoryImageMap = (Map<String, Object>) task.getResult().get("categoryImage");
                        Image categoryImage = new Image();
                        categoryImage.setId(String.valueOf(Objects.requireNonNull(categoryImageMap).get("id")));
                        categoryImage.setImageURL(String.valueOf(categoryImageMap.get("imageURL")));

                        ArrayList<Image> images = (ArrayList<Image>) task.getResult().get("images");

                        categoryMap.put("name", newCategoryName);
                        categoryMap.put("categoryImage", categoryImage);
                        categoryMap.put("images", images);

                        firebaseFirestore.collection("Categories")
                                .document(String.valueOf(categoryMap.get("name")))
                                .set(categoryMap)
                                .addOnSuccessListener(unused ->
                                        firebaseFirestore.collection("Categories")
                                                .document(category.getName())
                                                .delete()
                                                .addOnSuccessListener(unused12 -> Toast.makeText(context, "Η κατηγορία " + category.getName() + " ενημερώθηκε επιτυχώς", Toast.LENGTH_SHORT).show())
                                                .addOnFailureListener(e -> {
                                                    firebaseFirestore.collection("Categories")
                                                            .document(String.valueOf(categoryMap.get("name")))
                                                            .delete()
                                                            .addOnSuccessListener(unused1 -> {

                                                            })
                                                            .addOnFailureListener(e1 -> {

                                                            });

                                                    Toast.makeText(context, "Η ενημέρωση της κατηγορίας " + category.getName() + " απέτυχε", Toast.LENGTH_SHORT).show();
                                                }))
                                .addOnFailureListener(e -> Toast.makeText(context, "Η ενημέρωση της κατηγορίας " + category.getName() + " απέτυχε", Toast.LENGTH_SHORT).show());
                    }
                });

    }

    /*-------- Update selected category's image in firestore and then delete the previous one --------*/
    public void updateCategory(Category category, String url, String newImageName) {

        Image categoryImage = new Image();
        categoryImage.setId(newImageName);
        categoryImage.setImageURL(url);

        StorageReference prevImageReference = firebaseStorage.getReference().child("images/" + category.getCategoryImage().getId());

        firebaseFirestore.collection("Categories")
                .document(category.getName())
                .update("categoryImage", categoryImage)
                .addOnSuccessListener(unused -> {

                    prevImageReference.delete()
                            .addOnSuccessListener(unused1 -> {

                            })
                            .addOnFailureListener(e -> {

                            });

                    Toast.makeText(context, "Η κατηγορία " + category.getName() + " ενημερώθηκε επιτυχώς", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Η ενημέρωση της κατηγορίας " + category.getName() + " απέτυχε", Toast.LENGTH_SHORT).show());
    }

    /*-------- Update both selected category's name and the image in firestore  --------*/
    public void updateCategory(Category category, String newCategoryName, String newUrl, String newImageName) {

        firebaseFirestore.collection("Categories")
                .document(category.getName())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Map<String, Object> categoryMap = new HashMap<>();

                        Image categoryImage = new Image();
                        categoryImage.setId(newImageName);
                        categoryImage.setImageURL(newUrl);

                        ArrayList<Image> images = (ArrayList<Image>) task.getResult().get("images");

                        categoryMap.put("name", newCategoryName);
                        categoryMap.put("categoryImage", categoryImage);
                        categoryMap.put("images", images);

                        firebaseFirestore.collection("Categories")
                                .document(String.valueOf(categoryMap.get("name")))
                                .set(categoryMap)
                                .addOnSuccessListener(unused -> {

                                    // as the updated category object has been successfully added to collection
                                    // then delete the previous category object from firebase firestore collection
                                    firebaseFirestore.collection("Categories")
                                            .document(category.getName())
                                            .delete()
                                            .addOnSuccessListener(unused12 -> {

                                                // as the new category has been successfully been added to firestore
                                                // so we should delete the previous image reference from storage after all
                                                StorageReference prevImageReference = firebaseStorage.getReference().child("images/" + category.getCategoryImage().getId());
                                                prevImageReference.delete()
                                                        .addOnSuccessListener(unused13 -> {

                                                        })
                                                        .addOnFailureListener(e -> {

                                                        });

                                                Toast.makeText(context, "Η κατηγορία " + category.getName() + " ενημερώθηκε επιτυχώς", Toast.LENGTH_SHORT).show();
                                            })
                                            .addOnFailureListener(e -> {

                                                // as the process of deleting the old category has failed
                                                // then the new category should be deleted instead
                                                firebaseFirestore.collection("Categories")
                                                        .document(String.valueOf(categoryMap.get("name")))
                                                        .delete()
                                                        .addOnSuccessListener(unused1 -> {

                                                            // as the new category has been successfully deleted then
                                                            // it's image reference to storage should be deleted too
                                                            StorageReference newImageReference = firebaseStorage.getReference().child("images/" + newImageName);
                                                            newImageReference.delete()
                                                                    .addOnSuccessListener(unused2 -> {

                                                                    })
                                                                    .addOnFailureListener(e12 -> {

                                                                    });
                                                        })
                                                        .addOnFailureListener(e1 -> {

                                                        });

                                                Toast.makeText(context, "Η ενημέρωση της κατηγορίας " + category.getName() + " απέτυχε", Toast.LENGTH_SHORT).show();
                                            });

                                })
                                .addOnFailureListener(e -> {

                                    // failed to updated the category although uploading the image
                                    // was successful, so we should delete the uploaded image
                                    StorageReference newImageReference = firebaseStorage.getReference().child("images/" + newImageName);
                                    newImageReference.delete()
                                            .addOnSuccessListener(unused2 -> {

                                            })
                                            .addOnFailureListener(e12 -> {

                                            });

                                    Toast.makeText(context, "Η ενημέρωση της κατηγορίας " + category.getName() + " απέτυχε", Toast.LENGTH_SHORT).show();
                                });
                    }
                });

    }

    /*----- Uploading category image to firebase storage -----*/
    public void uploadImage(Category category, String updatedCategoryName, Uri imageUri, final FirebaseDataCategoriesAsyncResponse callback) {

        /*----- Variables -----*/
        String imageName = updatedCategoryName + System.currentTimeMillis();
        StorageReference newImageReference = firebaseStorage.getReference().child("images/" + imageName);

        /*----- Uploading Image To Firebase Storage -----*/
        newImageReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> newImageReference.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            if (callback != null)
                                callback.processUpdateCategoryImageFinished(uri.toString(), imageName);
                            else
                                Toast.makeText(context, "Η ενημέρωση της κατηγορίας " + category.getName() + " απέτυχε", Toast.LENGTH_SHORT).show();

                        })
                        .addOnFailureListener(e -> Toast.makeText(context, "Η ενημέρωση της κατηγορίας " + category.getName() + " απέτυχε", Toast.LENGTH_SHORT).show()))
                .addOnFailureListener(e -> Toast.makeText(context, "Η ενημέρωση της κατηγορίας " + category.getName() + " απέτυχε", Toast.LENGTH_SHORT).show());
    }

    /*-------- Delete category object from firestore and if it succeed
               then delete it's image reference from storage. In the
               end, delete all category's images from storage too. --------*/
    public void deleteCategory(Category category) {

        ArrayList<Image> categoryImages = category.getImages();

        firebaseFirestore.collection("Categories")
                .document(category.getName())
                .delete()
                .addOnSuccessListener(unused -> {

                    Toast.makeText(context, "Η κατηγορία " + category.getName() + " διαγράφηκε επιτυχώς", Toast.LENGTH_SHORT).show();

                    StorageReference imageReference = firebaseStorage.getReference().child("images/" + category.getCategoryImage().getId());

                    imageReference.delete()
                            .addOnSuccessListener(unused1 -> {

                                /*-------- Deleting all category images from storage --------*/
                                for (Image imageToDelete : categoryImages) {
                                    StorageReference imageToDeleteReference = firebaseStorage.getReference().child("images/" + imageToDelete.getId());

                                    imageToDeleteReference.delete()
                                            .addOnSuccessListener(unused2 -> {

                                            })
                                            .addOnFailureListener(e -> {

                                            });
                                }

                            })
                            .addOnFailureListener(e -> {

                            });

                })
                .addOnFailureListener(e -> Toast.makeText(context, "Αποτυχία διαγραφής της κατηγορίας " + category.getName(), Toast.LENGTH_SHORT).show());

    }

}
