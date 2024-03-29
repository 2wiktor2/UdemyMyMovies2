package com.wiktor.mymovies2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.wiktor.mymovies2.adapters.MovieAdapter;
import com.wiktor.mymovies2.data.FavouriteMovie;
import com.wiktor.mymovies2.data.MainViewModel;
import com.wiktor.mymovies2.data.Movie;

import java.util.ArrayList;
import java.util.List;

public class ActivityFavourite extends AppCompatActivity {

    private RecyclerView recyclerViewFavouriteMovies;
    private MovieAdapter adapter;
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);
        recyclerViewFavouriteMovies = findViewById(R.id.recyclerViewFavouriteMovies);
        recyclerViewFavouriteMovies.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new MovieAdapter();
        recyclerViewFavouriteMovies.setAdapter(adapter);

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        LiveData<List<FavouriteMovie>> favouriteMovies = viewModel.getFavouriteMovies();
        favouriteMovies.observe(this, new Observer<List<FavouriteMovie>>() {
            @Override
            public void onChanged(List<FavouriteMovie> favouriteMovies) {
                List<Movie> movies = new ArrayList<>();
                if (favouriteMovies != null){
                    movies.addAll(favouriteMovies);
                    adapter.setMovies(movies);
                }
            }
        });


        adapter.setOnPosterClickListener(new MovieAdapter.OnPosterClickListener() {
            @Override
            public void onPosterClick(int position) {
                // Toast.makeText(MainActivity.this, "Clicked: " + position, Toast.LENGTH_SHORT).show();
                Movie movie = adapter.getMovies().get(position);
                Intent intent = new Intent(ActivityFavourite.this, ActivityDetail.class);
                intent.putExtra("id", movie.getId());
                startActivity(intent);
            }
        });
    }

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
}