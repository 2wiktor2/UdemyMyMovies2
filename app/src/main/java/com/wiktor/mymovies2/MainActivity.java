package com.wiktor.mymovies2;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wiktor.mymovies2.adapters.MovieAdapter;
import com.wiktor.mymovies2.data.Movie;
import com.wiktor.mymovies2.utils.JSONUtils;
import com.wiktor.mymovies2.utils.NetworkUtils;

import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Switch switchSort;
    private RecyclerView recyclerViewPosters;
    private MovieAdapter movieAdapter;
    private TextView textViewTopRated;
    private TextView textViewTopPopularity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switchSort = findViewById(R.id.switchSort);
        textViewTopRated = findViewById(R.id.textViewTopRated);
        textViewTopPopularity = findViewById(R.id.textViewPopularity);

        recyclerViewPosters = findViewById(R.id.recyclerViewPosters);
        recyclerViewPosters.setLayoutManager(new GridLayoutManager(this, 3));
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
        //Получаем список фильмов
        JSONObject jsonObject = NetworkUtils.getJSONFrmNetwork(methodOfSort, 1);
        //получаем список фильмов
        ArrayList<Movie> movies = JSONUtils.getMoviesFromJSON(jsonObject);
        movieAdapter.setMovies(movies);
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