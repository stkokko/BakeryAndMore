package com.example.bakeryandmore.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.example.bakeryandmore.R;

import java.security.spec.ECField;

public class LoadingDialog {

    /*----- Variables -----*/
    private final Context context;
    private AlertDialog dialog;

    /*----- Constructor -----*/
    public LoadingDialog(Context context) {
        this.context = context;
    }

    /*----- Create and show loading dialog -----*/
    public void startLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.loading_dialog, null);

        builder.setView(view);
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();
    }

    /*----- Dismiss/Close loading dialog -----*/
    public void dismissDialog() {
        try {
            dialog.dismiss();
        } catch (Exception e) {
            Log.d("myException", "dismissDialog: " + e.getMessage());
        }

    }
}
