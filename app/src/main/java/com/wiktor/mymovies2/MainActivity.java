package com.wiktor.mymovies2;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wiktor.mymovies2.adapters.MovieAdapter;
import com.wiktor.mymovies2.data.MainViewModel;
import com.wiktor.mymovies2.data.Movie;
import com.wiktor.mymovies2.utils.JSONUtils;
import com.wiktor.mymovies2.utils.NetworkUtils;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks {

    private Switch switchSort;
    private RecyclerView recyclerViewPosters;
    private MovieAdapter movieAdapter;
    private TextView textViewTopRated;
    private TextView textViewTopPopularity;

    private MainViewModel viewModel;

    //любое число
    final private static int LOADER_ID = 155;
    private LoaderManager loaderManager;

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
        setContentView(R.layout.activity_main);

        loaderManager = LoaderManager.getInstance(this);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        switchSort = findViewById(R.id.switchSort);
        textViewTopRated = findViewById(R.id.textViewTopRated);
        textViewTopPopularity = findViewById(R.id.textViewPopularity);

        recyclerViewPosters = findViewById(R.id.recyclerViewPosters);
        recyclerViewPosters.setLayoutManager(new GridLayoutManager(this, 2));
        movieAdapter = new MovieAdapter();
        //Устанавливаем адаптер у RecyclerView
        recyclerViewPosters.setAdapter(movieAdapter);

        switchSort.setChecked(true);
        switchSort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                setMethodOfSort(b);
            }
        });
        switchSort.setChecked(false);

        //Нажатие на постер к фильму
        movieAdapter.setOnPosterClickListener(new MovieAdapter.OnPosterClickListener() {
            @Override
            public void onPosterClick(int position) {
                //Toast.makeText(MainActivity.this, "Clicked : " + position, Toast.LENGTH_SHORT).show();
                Movie movie = movieAdapter.getMovies().get(position);
                Intent intent = new Intent(MainActivity.this, ActivityDetail.class);
                intent.putExtra("id", movie.getId());
                startActivity(intent);
            }
        });

        // Конец списка
        movieAdapter.setOnReachEndListener(new MovieAdapter.OnReachEndListener() {
            @Override
            public void onReachEnd() {
               // Toast.makeText(MainActivity.this, "конец списка", Toast.LENGTH_SHORT).show();

            }
        });

        LiveData<List<Movie>> moviesFromLiveData = viewModel.getMovies();
        moviesFromLiveData.observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> movies) {
                movieAdapter.setMovies(movies);
            }
        });

    }

    public void onClickSetPopularity(View view) {
        setMethodOfSort(false);
        switchSort.setChecked(false);
    }

    public void onClickSetTopRated(View view) {
        setMethodOfSort(true);
        switchSort.setChecked(true);
    }

    private void setMethodOfSort(Boolean isTopRated) {
        int methodOfSort;
        if (isTopRated) {
            methodOfSort = NetworkUtils.TOP_RATED;
            textViewTopRated.setTextColor(getResources().getColor(R.color.colorAccent));
            textViewTopPopularity.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhite));
        } else {
            methodOfSort = NetworkUtils.POPULARITY;
            textViewTopRated.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhite));
            textViewTopPopularity.setTextColor(getResources().getColor(R.color.colorAccent));
        }

        downloadData(methodOfSort, 1);
    }

    private void downloadData(int methodOfSort, int page) {
/*        //Получаем список фильмов
        JSONObject jsonObject = NetworkUtils.getJSONFromNetwork(methodOfSort, page);
        //получаем список фильмов
        ArrayList<Movie> movies = JSONUtils.getMoviesFromJSON(jsonObject);

        if (movies != null && !movies.isEmpty()) {
            viewModel.daleteAllMovies();
            for (Movie movie : movies) {
                viewModel.insertMovie(movie);
            }
        }*/

        URL url = NetworkUtils.buildURL(methodOfSort, page);
        Bundle bundle = new Bundle();
        bundle.putString("url", url.toString());

        //Запускаем загрузчик
        loaderManager.restartLoader(LOADER_ID, bundle, this);

    }

    @NonNull
    @Override
    public Loader onCreateLoader(int id, @Nullable Bundle args) {
        NetworkUtils.JSONLoader jsonLoader = new NetworkUtils.JSONLoader(this, args);
        return jsonLoader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data) {
        //получаем список фильмов
        ArrayList<Movie> movies = JSONUtils.getMoviesFromJSON((JSONObject) data);

        if (movies != null && !movies.isEmpty()) {
            viewModel.daleteAllMovies();
            for (Movie movie : movies) {
                viewModel.insertMovie(movie);
            }
        }
        loaderManager.destroyLoader(LOADER_ID);
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }
}
