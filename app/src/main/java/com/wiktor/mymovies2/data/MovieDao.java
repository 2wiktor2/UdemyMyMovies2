package com.wiktor.mymovies2.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

//@Dao --
@Dao
public interface MovieDao {
    // @Query()  --- обозначает запрос. "SELECT * FROM movies" --- Сам запрос в бд
    @Query("SELECT * FROM movies")
    //Получить все фильмы из бд
    LiveData<List<Movie>> getAllMovies();


    // Метод который возвращает фильм по его ID. Возвращает объект Movie, принимает int ID фильма
    // "SELECT * FROM movies WHERE id == :movieId"  --- select всё из бд movies где id == movieId (параметр который приходит в метод)
    @Query("SELECT * FROM movies WHERE id == :movieId")
    Movie getMovieById(int movieId);

    // Метод который удаляет все фильмы из бд
    @Query("DELETE FROM movies")
    void deleteAllMovies();

    //Метод для всавки данных
    @Insert
    void insertMovie(Movie movie);

    //Метод для удаления одного элемента
    @Delete
    void deleteMovie(Movie movie);
}
