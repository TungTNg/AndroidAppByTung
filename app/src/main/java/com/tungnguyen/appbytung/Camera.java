package com.tungnguyen.appbytung;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Camera {
    private final String description;
    private final String imageURL;
    private final Double latitude;
    private final Double longitude;

    public Camera(String description, String imageURL, Double latitude, Double longitude) {
        this.description = description;
        this.imageURL = imageURL;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public interface VolleyResponseListener
    {
        void onError(String errorMessage);

        void onResponse(List<Camera> cameraList);
    }

    public String getDescription() {
        return description;
    }

    public String getImageURL() {
        return imageURL;
    }

    public Double getLatitude() { return latitude; }

    public Double getLongitude() { return longitude; }

    public static void loadCameraData(Context context, VolleyResponseListener volleyResponseListener) {
        String cameraApiURL = "https://web6.seattle.gov/Travelers/api/Map/Data?zoomId=13&type=2";

        // Request a Object response from the provided URL.
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, cameraApiURL, null,
                response -> {
                    ArrayList<Camera> cameraList = new ArrayList<>();

                    try {
                        JSONArray featuresArray = response.getJSONArray("Features");
                        for (int i = 0; i < featuresArray.length(); i++) {
                            JSONObject feature = featuresArray.getJSONObject(i);

                            JSONArray cameraPointCoordinate = feature.getJSONArray("PointCoordinate");
                            Double cameraLatitude = Double.valueOf(cameraPointCoordinate.get(0).toString());
                            Double cameraLongitude = Double.valueOf(cameraPointCoordinate.get(1).toString());

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

                                cameraList.add(new Camera(cameraDescription, cameraImageURL, cameraLatitude, cameraLongitude));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    volleyResponseListener.onResponse(cameraList);
                }, error -> volleyResponseListener.onError(error.getMessage()));

        // Add the request to the RequestQueue.
        VolleySingleton.getInstance(context).addToRequestQueue(objectRequest);
    }
}
