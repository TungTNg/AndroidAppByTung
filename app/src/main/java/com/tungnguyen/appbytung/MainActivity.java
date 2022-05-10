package com.tungnguyen.appbytung;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get grid
        GridView mainGrid = findViewById(R.id.mainGrid);

        // Create buttons to add to grid
        ArrayList<Button> buttonList = new ArrayList<>();
        String[] buttonNames = {"Movies", "Cameras", "Google Map", "Traffic", "Music", "Food" };
        for(String buttonName : buttonNames) {
            Button newBtn = new Button(this);
            newBtn.setText(buttonName);
            buttonList.add(newBtn);
        }

        // Add buttons to grid by using custom adapter
        ArrayAdapter<Button> buttonArrayAdapter = new CustomButtonAdapter(this, buttonList);
        mainGrid.setAdapter(buttonArrayAdapter);
    }

}