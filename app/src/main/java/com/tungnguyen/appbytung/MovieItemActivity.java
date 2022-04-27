package com.tungnguyen.appbytung;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class MovieItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_item_activity);

        Bundle extraInfo = getIntent().getExtras();
        if (extraInfo != null) {
            TextView movieName = findViewById(R.id.movieItemName);
            TextView movieYear = findViewById(R.id.movieItemYear);
            TextView movieDirector = findViewById(R.id.movieItemDirector);
            TextView movieDescription = findViewById(R.id.movieItemDescription);
            ImageView movieImage = findViewById(R.id.movieItemImage);

            movieName.setText(extraInfo.getString("MovieName"));
            movieYear.setText(extraInfo.getString("MovieYear"));
            movieDirector.setText(extraInfo.getString("MovieDirector"));
            movieDescription.setText(extraInfo.getString("MovieDescription"));
            Picasso.get()
                    .load(extraInfo.getString("MovieImageURL"))
                    .fit().centerInside()
                    .into(movieImage);
            }



    }
}