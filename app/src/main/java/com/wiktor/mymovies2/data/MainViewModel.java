package com.wiktor.mymovies2.data;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;

//объект ViewModel

public class MainViewModel extends AndroidViewModel {

    //Создаем объект базы данных
    private static MovieDataBase dataBase;
    //Список фильмов
    private LiveData<List<Movie>> movies;


    // переопределяем конструктор
    public MainViewModel(@NonNull Application application) {
        super(application);
        dataBase = MovieDataBase.getInstance(getApplication());
        movies = dataBase.movieDao().getAllMovies();
    }

    // методы для доступа к данным


    //Метод возвращает объект Movie
    public Movie getMovieById(int id) {
        //Все действия в этом методе нужно выполнить в другом программном потоке, для этого создаем класс GetMovieTask
        try {
            return new GetMovieTask().execute(id).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class GetMovieTask extends AsyncTask<Integer, Void, Movie> {

        @Override
        protected Movie doInBackground(Integer... integers) {
            if (integers != null && integers.length > 0) {
                return dataBase.movieDao().getMovieById(integers[0]);
            }
            return null;
        }
    }

}
