package com.example.langapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.Nullable;

public class PopUpDialog extends Activity {
    Button getChoice;
    ListView myList;
    String [] listContent = {"Jovanka", "Milica", "Jovica", "Ilija"};
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_dialog_layout);

        myList = (ListView)findViewById(R.id.list);
        getChoice = findViewById(R.id.assign_button);


    }


}
