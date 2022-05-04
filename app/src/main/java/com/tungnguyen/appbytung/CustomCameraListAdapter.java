package com.tungnguyen.appbytung;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CustomCameraListAdapter extends RecyclerView.Adapter<CustomCameraListAdapter.ViewHolder> {
    private final ArrayList<Camera> localDataSet;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView cameraDescriptionView;
        private final ImageView cameraImageView;

        public ViewHolder(View view) {
            super(view);

            cameraDescriptionView = view.findViewById(R.id.cameraListItemDescription);
            cameraImageView = view.findViewById(R.id.cameraListItemImage);
        }

        public TextView getCameraDescriptionView() {
            return cameraDescriptionView;
        }

        public ImageView getCameraImageView() {
            return cameraImageView;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView.
     */
    public CustomCameraListAdapter(ArrayList<Camera> dataSet) {
        localDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.camera_list_row_layout, viewGroup, false);

        // Set each item height to be 1/2 of parent
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = viewGroup.getHeight() / 2;
        view.setLayoutParams(params);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        Camera camera = localDataSet.get(position);
        viewHolder.getCameraDescriptionView().setText(String.valueOf(camera.getDescription()));
        Picasso .get()
                .load(camera.getImageURL())
                .into(viewHolder.getCameraImageView());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

}
