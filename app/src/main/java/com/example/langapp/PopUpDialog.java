package com.example.langapp;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

public class PopUpDialog extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_dialog_layout);
    }
}
