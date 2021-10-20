package com.wiktor.mymovies2.utils;

import com.wiktor.mymovies2.data.Movie;
import com.wiktor.mymovies2.data.Review;
import com.wiktor.mymovies2.data.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

//здесь преобразуется JSON в объект
public class JSONUtils {

    // Ключи по которым будем разбирать JSON

    //Ключ для JSONArray
    private static final String KEY_RESULTS = "results";

    // Для отзывов
    private static final String KEY_AUTHOR = "author";
    private static final String KEY_CONTENT = "content";
    // Для видео
    private static final String KEY_KEY_OF_VIDEO = "key";
    private static final String KEY_NAME = "name";
    private static final String BASE_YOUTUBE_URL = "https://www.youtube.com/watch?v=";
    // Вся информация о фильме
    private static final String KEY_VOTE_COUNT = "vote_count";
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_ORIGINAL_TITLE = "original_title";
    private static final String KEY_OVERVIEW = "overview";
    private static final String KEY_POSTER_PATH = "poster_path";
    private static final String KEY_BACKDROP_PATH = "backdrop_path";
    private static final String KEY_VOTE_AVERAGE = "vote_average";
    private static final String KEY_RELEASE_DATE = "release_date";

    //Базовая ссылка для загрузки картинки
    private  static final String BASE_POSTER_URL = "https://image.tmdb.org/t/p/";

    //размеры для постеров
    public  static final String SMALL_POSTER_SIZE = "w185";
    public  static final String BIG_POSTER_SIZE = "w780";


    // Здесь мы получаем массив с фильмаи
    public static ArrayList<Movie> getMoviesFromJSON(JSONObject jsonObject){
        //Массив для хранения фильмов
        ArrayList<Movie> result = new ArrayList<>();
        if(jsonObject == null){
            return result;
        }
        //Первым делом получаем JSONArray по ключу KEY_RESULTS
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(KEY_RESULTS);
            // из JSONArray в цикле получаем фильмы
            for (int i = 0; i<jsonArray.length(); i ++ ){
                //Получаем JSONObject
                JSONObject objectMovie = jsonArray.getJSONObject(i);
                //Получаем необходимые данные
                int id = objectMovie.getInt(KEY_ID);
                int voteCount = objectMovie.getInt(KEY_VOTE_COUNT);
                String title = objectMovie.getString(KEY_TITLE);
                String originalTitle = objectMovie.getString(KEY_ORIGINAL_TITLE);
                String overview = objectMovie.getString(KEY_OVERVIEW);
                String posterPath = BASE_POSTER_URL + SMALL_POSTER_SIZE + objectMovie.getString(KEY_POSTER_PATH);
                String bigPosterPath = BASE_POSTER_URL + BIG_POSTER_SIZE + objectMovie.getString(KEY_POSTER_PATH);
                String backdropPath = objectMovie.getString(KEY_BACKDROP_PATH);
                double voteAverage = objectMovie.getDouble(KEY_VOTE_AVERAGE);
                String releaseDate = objectMovie.getString(KEY_RELEASE_DATE);

                //Создаем объект Movie и запихиваем туда данные из Json
                Movie movie = new Movie(id, voteCount, title, originalTitle, overview, posterPath, bigPosterPath, backdropPath, voteAverage, releaseDate);
                //Полученный фильм добавляем в массив result
                result.add(movie);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static ArrayList<Review> getReviewsFromJSON(JSONObject jsonObject) {
        ArrayList<Review> result = new ArrayList<>();
        if (jsonObject == null) {
            return result;
        }
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(KEY_RESULTS);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObjectReview = jsonArray.getJSONObject(i);
                String author = jsonObjectReview.getString(KEY_AUTHOR);
                String content = jsonObjectReview.getString(KEY_CONTENT);
                Review review = new Review(author, content);
                result.add(review);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<Trailer> getTrailersFromJSON(JSONObject jsonObject) {
        ArrayList<Trailer> result = new ArrayList<>();
        if (jsonObject == null) {
            return result;
        }
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(KEY_RESULTS);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObjectTrailers = jsonArray.getJSONObject(i);
                String key = BASE_YOUTUBE_URL + jsonObjectTrailers.getString(KEY_KEY_OF_VIDEO);
                String name = jsonObjectTrailers.getString(KEY_NAME);
                Trailer trailer = new Trailer(key, name);
                result.add(trailer);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
}
