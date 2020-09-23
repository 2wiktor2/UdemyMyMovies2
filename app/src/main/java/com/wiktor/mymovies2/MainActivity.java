package com.wiktor.mymovies2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.wiktor.mymovies2.adapters.MovieAdapter;
import com.wiktor.mymovies2.data.Movie;
import com.wiktor.mymovies2.utils.JSONUtils;
import com.wiktor.mymovies2.utils.NetworkUtils;

import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewPosters;
    private MovieAdapter movieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerViewPosters = findViewById(R.id.recyclerViewPosters);
        recyclerViewPosters.setLayoutManager(new GridLayoutManager(this, 2));
        movieAdapter = new MovieAdapter();

        //Получаем список фильмов
        JSONObject jsonObject = NetworkUtils.getJSONFrmNetwork(NetworkUtils.POPULARITY, 1);
        //получаем список фильмов
        ArrayList<Movie> movies = JSONUtils.getMoviesFromJSON(jsonObject);
        //Устанавливаем фильмы у адаптера
        movieAdapter.setMovies(movies);
        //Устанавливаем адаптер у RecyclerView
        recyclerViewPosters.setAdapter(movieAdapter);










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






    }
}