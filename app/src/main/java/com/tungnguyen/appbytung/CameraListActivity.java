package com.tungnguyen.appbytung;


import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CameraListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_list_activity);

        if(isNetworkAvailable()) {
            Camera.loadCameraData(this, new Camera.VolleyResponseListener() {
                @Override
                public void onError(String errorMessage) {
                    Log.d("JSON", "Error: " + errorMessage);
                }

                @Override
                public void onResponse(List<Camera> cameraList) {
                    RecyclerView cameraListRecyclerView = findViewById(R.id.cameraListRecyclerView);
                    CustomCameraListAdapter cameraListAdapter = new CustomCameraListAdapter((ArrayList<Camera>) cameraList);
                    cameraListRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    cameraListRecyclerView.setAdapter(cameraListAdapter);
                }
            });
        } else {
            Toast.makeText(CameraListActivity.this, "You currently DO NOT have ANY internet connection!", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isAvailable() && activeNetworkInfo.isConnected();
    }
}