package edu.neu.madcourse.musicloud.dashboard;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import androidx.fragment.app.Fragment;

import edu.neu.madcourse.musicloud.R;

public class Loading {
    Fragment fragment;
    AlertDialog alertDialog;

    Loading(Fragment fragment){
        this.fragment = fragment;

    }

    void startLoading(){
        AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getContext());
        LayoutInflater inflater = fragment.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.loading,null));
        builder.setCancelable(true);

        alertDialog = builder.create();
        alertDialog.show();
    }
    void dismissDialog(){
        alertDialog.dismiss();
    }
}
