package com.tungnguyen.appbytung;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CameraListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_list_activity);

        ArrayList<Camera> cameraList = new ArrayList<>();

        RecyclerView cameraListRecyclerView = findViewById(R.id.cameraListRecyclerView);
        CustomCameraListAdapter cameraListAdapter = new CustomCameraListAdapter(cameraList);
        cameraListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cameraListRecyclerView.setAdapter(cameraListAdapter);

        if(isNetworkAvailable()) {
            // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(this);
            String cameraApiURL = "https://web6.seattle.gov/Travelers/api/Map/Data?zoomId=13&type=2";

            // Request a Object response from the provided URL.
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, cameraApiURL, null,
                response -> {
                    try {
                        JSONArray featuresArray = response.getJSONArray("Features");
                        for (int i = 0; i < featuresArray.length(); i++) {
                            JSONObject feature = featuresArray.getJSONObject(i);
                            JSONArray camerasArray = feature.getJSONArray("Cameras");

                            for (int j = 0; j < camerasArray.length(); j++) {
                                JSONObject camera = camerasArray.getJSONObject(j);
                                String cameraDescription = camera.getString("Description");
                                String cameraImageURL = camera.getString("ImageUrl");
                                String cameraType = camera.getString("Type");

                                if (cameraType.equals("sdot"))
                                    cameraImageURL = "https://www.seattle.gov/trafficcams/images/" + cameraImageURL;
                                else {
                                    cameraImageURL = "https://images.wsdot.wa.gov/nw/" + cameraImageURL;
                                }

                                cameraList.add(new Camera(cameraDescription, cameraImageURL));
                            }
                        }

                        // Trigger refresh of recycler view
                        cameraListAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Log.d("JSON", "Error: " + error.getMessage()));

            // Add the request to the RequestQueue.
            queue.add(objectRequest);
        } else {
            Toast.makeText(CameraListActivity.this, "You currently DO NOT have ANY internet connection!", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isAvailable() && activeNetworkInfo.isConnected();
    }
}