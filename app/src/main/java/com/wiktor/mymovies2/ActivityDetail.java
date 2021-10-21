package com.wiktor.mymovies2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.wiktor.mymovies2.adapters.ReviewAdapter;
import com.wiktor.mymovies2.adapters.TrailerAdapter;
import com.wiktor.mymovies2.data.FavouriteMovie;
import com.wiktor.mymovies2.data.MainViewModel;
import com.wiktor.mymovies2.data.Movie;
import com.wiktor.mymovies2.data.Review;
import com.wiktor.mymovies2.data.Trailer;
import com.wiktor.mymovies2.utils.JSONUtils;
import com.wiktor.mymovies2.utils.NetworkUtils;

import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityDetail extends AppCompatActivity {

    private ImageView imageViewAddToFavourite;
    private ImageView imageViewBigPoster;
    private TextView textViewTitle;
    private TextView textViewOriginalTitle;
    private TextView textViewRating;
    private TextView textViewReleaseDate;
    private TextView textViewOverview;

    private ScrollView scrollViewInfo;

    private int id;
    private MainViewModel viewModel;
    private Movie movie;
    private FavouriteMovie favouriteMovie;

    private RecyclerView recyclerViewTrailers;
    private RecyclerView recyclerViewReviews;

    private ReviewAdapter reviewAdapter;
    private TrailerAdapter trailerAdapter;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.itemMain:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.itemFavourite:
                Intent intentToFavourite = new Intent(this, ActivityFavourite.class);
                startActivity(intentToFavourite);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        scrollViewInfo = findViewById(R.id.scrollViewInfo);

        imageViewAddToFavourite = findViewById(R.id.imageViewAddToFavorite);
        imageViewBigPoster = findViewById(R.id.imageViewBigPoster);
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewOriginalTitle = findViewById(R.id.textViewOriginalTitle);
        textViewRating = findViewById(R.id.textViewRating);
        textViewReleaseDate = findViewById(R.id.textViewReleaseDate);
        textViewOverview = findViewById(R.id.textViewOverView);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("id")) {
            id = intent.getIntExtra("id", -1);
        } else {
            finish();
        }

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        movie = viewModel.getMovieById(id);
        if (movie != null) {
            Picasso.get().load(movie.getBigPosterPath()).placeholder(R.drawable.abc_vector_test).into(imageViewBigPoster);
            textViewTitle.setText(movie.getTitle());
            textViewOriginalTitle.setText(movie.getOriginalTitle());
            textViewRating.setText(Double.toString(movie.getVoteAverage()));
            textViewReleaseDate.setText(movie.getReleaseDate());
            textViewOverview.setText(movie.getOverview());

            setFavourite();


            recyclerViewTrailers = findViewById(R.id.recyclerViewTrailers);
            recyclerViewReviews = findViewById(R.id.recyclerViewReviews);

            trailerAdapter = new TrailerAdapter();
            reviewAdapter = new ReviewAdapter();

            //Установка слушателя для нажатия по названию трейлера
            trailerAdapter.setOnTrailerClickListener(new TrailerAdapter.OnTrailerClickListener() {
                @Override
                public void onTrailerClick(String url) {
                    //Toast.makeText(ActivityDetail.this, "url = " + url, Toast.LENGTH_SHORT).show();
                    Intent intentToTrailer = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intentToTrailer);
                }
            });

            recyclerViewTrailers.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));

            recyclerViewTrailers.setAdapter(trailerAdapter);
            recyclerViewReviews.setAdapter(reviewAdapter);

            JSONObject jsonObjectTrailers = NetworkUtils.getJSONForVideos(movie.getId());
            JSONObject jsonObjectReviews = NetworkUtils.getJSONForReviews(movie.getId());

            ArrayList<Trailer> trailers = JSONUtils.getTrailersFromJSON(jsonObjectTrailers);
            ArrayList<Review> reviews = JSONUtils.getReviewsFromJSON(jsonObjectReviews);

            trailerAdapter.setTrailers(trailers);
            reviewAdapter.setReviews(reviews);

            scrollViewInfo.smoothScrollTo(0, 0);
        }

    }

    private void setFavourite() {
        favouriteMovie = viewModel.getFavouriteMovieById(id);
        if (favouriteMovie == null) {
            imageViewAddToFavourite.setImageResource(R.drawable.ic_gray_star_black_24dp);
        } else {
            imageViewAddToFavourite.setImageResource(R.drawable.ic_yellow_star_black_24dp);
        }
    }

    public void onClickChangeFavorite(View view) {
        if (favouriteMovie == null) {
            viewModel.insertFavouriteMovie(new FavouriteMovie(movie));
            Toast.makeText(this, R.string.add_to_favourite, Toast.LENGTH_SHORT).show();
        } else {
            viewModel.deleteFavouriteMovie(favouriteMovie);
            Toast.makeText(this, R.string.remove_from_favourite, Toast.LENGTH_SHORT).show();
        }
        setFavourite();
    }
}