package com.tungnguyen.appbytung;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

public class CustomMovieListAdapter extends RecyclerView.Adapter<CustomMovieListAdapter.ViewHolder> {
    private final String[][] localDataSet;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView movieNameView;
        private final TextView movieYearView;
        private final ImageView movieImageView;

        public ViewHolder(View view) {
            super(view);

            movieNameView = view.findViewById(R.id.cameraListItemDescription);
            movieYearView = view.findViewById(R.id.movieListItemYear);
            movieImageView = view.findViewById(R.id.cameraListItemImage);
        }

        public TextView getMovieNameView() {
            return movieNameView;
        }

        public TextView getMovieYearView() {
            return movieYearView;
        }

        public ImageView getMovieImageView() {
            return movieImageView;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView.
     */
    public CustomMovieListAdapter(String[][] dataSet) {
        localDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.movie_list_row_layout, viewGroup, false);

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
        viewHolder.getMovieNameView().setText(String.valueOf(localDataSet[position][0]));
        viewHolder.getMovieYearView().setText(localDataSet[position][1]);
        Picasso .get()
                .load(localDataSet[position][3])
                .into(viewHolder.getMovieImageView());

        viewHolder.getMovieImageView().setOnClickListener(view -> {
            Intent movieItemIntent = new Intent(view.getContext(), MovieItemActivity.class);
            movieItemIntent.putExtra("MovieName", localDataSet[position][0]);
            movieItemIntent.putExtra("MovieYear", localDataSet[position][1]);
            movieItemIntent.putExtra("MovieDirector", localDataSet[position][2]);
            movieItemIntent.putExtra("MovieImageURL", localDataSet[position][3]);
            movieItemIntent.putExtra("MovieDescription", localDataSet[position][4]);
            view.getContext().startActivity(movieItemIntent);
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.length;
    }

}
