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

    public LiveData<List<Movie>> getMovies() {
        return movies;
    }

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

    public void daleteAllMovies() {
        new DeleteMoviesTask().execute();
    }

    public void insertMovie(Movie movie) {
        new InsertTask().execute(movie);
    }

    public void deleteMovie(Movie movie) {
        new DeleteTask().equals(movie);
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

    private static class DeleteMoviesTask extends AsyncTask<Void, Void, Movie> {

        @Override
        protected Movie doInBackground(Void... voids) {
            dataBase.movieDao().deleteAllMovies();
            return null;
        }
    }

    private static class InsertTask extends AsyncTask<Movie, Void, Movie> {

        @Override
        protected Movie doInBackground(Movie... movies) {
            if (movies != null && movies.length > 0) {
                dataBase.movieDao().insertMovie(movies[0]);
            }
            return null;
        }
    }

    private static class DeleteTask extends AsyncTask<Movie, Void, Void> {

        @Override
        protected Void doInBackground(Movie... movies) {
            if (movies != null && movies.length > 0) {
                dataBase.movieDao().deleteMovie(movies[0]);
            }
            return null;
        }
    }
}
