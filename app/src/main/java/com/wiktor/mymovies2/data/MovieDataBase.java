package com.wiktor.mymovies2.data;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

//Сама база данных
//Помечаем класс аннотацией @Database() c параметрами:
// entities = {Movie.class},   --- таблицы в бд
// version = 1, --- версия бд
// exportSchema = false
@Database(entities = {Movie.class, FavouriteMovie.class}, version = 3, exportSchema = false)
public abstract class MovieDataBase extends RoomDatabase {

    private static final String DB_NAME = "movies.db";
    private static MovieDataBase dataBase;

    //блок синхронизации. Для того что бы не было проблем доступа из разных потоков
    private static final Object LOCK = new Object();

    // синглтон
    public static MovieDataBase getInstance(Context context){
        synchronized (LOCK){
            if (dataBase == null){
                dataBase = Room.databaseBuilder(context, MovieDataBase.class, DB_NAME).fallbackToDestructiveMigration().build();
            }
        }
        return dataBase;
    }

    // Метод который возвращает Dao
    public abstract MovieDao movieDao();

}
