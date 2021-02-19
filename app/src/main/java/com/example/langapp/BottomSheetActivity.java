package com.example.langapp;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

public class BottomSheetActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_bottom_sheet);
        System.out.println("jeste");
    }
}
