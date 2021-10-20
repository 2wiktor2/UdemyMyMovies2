package com.wiktor.mymovies2;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wiktor.mymovies2.adapters.MovieAdapter;
import com.wiktor.mymovies2.data.MainViewModel;
import com.wiktor.mymovies2.data.Movie;
import com.wiktor.mymovies2.utils.JSONUtils;
import com.wiktor.mymovies2.utils.NetworkUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Switch switchSort;
    private RecyclerView recyclerViewPosters;
    private MovieAdapter movieAdapter;
    private TextView textViewTopRated;
    private TextView textViewTopPopularity;

    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                Toast.makeText(MainActivity.this, "Clicked : " + position, Toast.LENGTH_SHORT).show();
            }
        });

        // Конец списка
        movieAdapter.setOnReachEndListener(new MovieAdapter.OnReachEndListener() {
            @Override
            public void onReachEnd() {
                Toast.makeText(MainActivity.this, "конец списка", Toast.LENGTH_SHORT).show();

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
            textViewTopPopularity.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhite));
            textViewTopPopularity.setTextColor(getResources().getColor(R.color.colorAccent));
        }

        downloadData(methodOfSort, 1);
    }

    private void downloadData(int methodOfSort, int page) {
        //Получаем список фильмов
        JSONObject jsonObject = NetworkUtils.getJSONFrmNetwork(methodOfSort, page);
        //получаем список фильмов
        ArrayList<Movie> movies = JSONUtils.getMoviesFromJSON(jsonObject);

        if (movies != null && !movies.isEmpty()) {
            viewModel.daleteAllMovies();
            for (Movie movie : movies) {
                viewModel.insertMovie(movie);
            }
        }
    }
}


//JSONObject jsonObject = NetworkUtils.getJSONFrmNetwork(me)


//String url = NetworkUtils.buildURL(NetworkUtils.POPULARITY, 1).toString();
//Log.i("qwertyu", url);

/*        JSONObject jsonObject = NetworkUtils.getJSONFrmNetwork(NetworkUtils.TOP_RATED, 3);
        if (jsonObject == null){
            Toast.makeText(this, "Произошла ошибка", Toast.LENGTH_SHORT).show();
        } else Toast.makeText(this, "Успешно", Toast.LENGTH_SHORT).show();

        Log.i("qwertyu", jsonObject.toString());*/


/*        ArrayList<Movie> movies = JSONUtils.getMoviesFromJSON(jsonObject);
        // выводим в лог список всех фильмов
        StringBuilder builder = new StringBuilder();
        for (Movie movie : movies){
            builder.append(movie.getTitle()).append("\n");
        }
        Log.i("qwertyu", builder.toString());*/