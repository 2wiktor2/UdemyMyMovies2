package com.wiktor.mymovies2.utils;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

// Здесь осуществляется вся работа связанная с сетью
public class NetworkUtils {

    // Параметры для запроса в сеть


    //Базовая ссылка
    private static final String BASE_URL = "https://api.themoviedb.org/3/discover/movie";

    //Базовая ссылка для загрузки картинки
    private static final String BASE_POSTER_URL = "https://image.tmdb.org/t/p/";

    //размеры для постеров
    public static final String SMALL_POSTER_SIZE = "w185";
    public static final String BIG_POSTER_SIZE = "w780";


    //трейлеры и отзывы
    private static final String BASE_URL_VIDEOS = "https://api.themoviedb.org/3/movie/%s/videos";
    private static final String BASE_URL_REVIEWS = "https://api.themoviedb.org/3/movie/%s/reviews";

    //Строки для названия параметров в запросе
    private static final String PARAMS_API_KEY = "api_key";
    private static final String PARAMS_LANGUAGE = "language";
    private static final String PARAMS_SORT_BY = "sort_by";
    private static final String PARAMS_PAGE = "page";
    private static final String PARAMS_MIN_VOTE_COUNT = "vote_count.gte";


    //Значения для параметров
    private static final String API_KEY = "c925e78c54a854734b5ece1644e0bd48";
    private static final String LANGUAGE_VALUE = "ru-RU";
    private static final String SORT_BY_POPULARITY = "popularity.desc";
    private static final String SORT_BY_TOP_RATED = "vote_average.desc";
    private static final String MIN_VOTE_COUNT_VALUE = "1000";

    public static final int POPULARITY = 0;
    public static final int TOP_RATED = 1;


    //Построение ссылки для загрузки трейлера
    public static URL buildUrlToVideos(int id) {
        Uri uri = Uri.parse(String.format(BASE_URL_VIDEOS, id)).buildUpon()
                .appendQueryParameter(PARAMS_API_KEY, API_KEY)
                .appendQueryParameter(PARAMS_LANGUAGE, LANGUAGE_VALUE)
                .build();
        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Метод возвращает URL к отзывам
    public static URL buildUrlToReviews(int id) {
        Uri uri = Uri.parse(String.format(BASE_URL_REVIEWS, id)).buildUpon()
                .appendQueryParameter(PARAMS_API_KEY, API_KEY)
                //.appendQueryParameter(PARAMS_LANGUAGE, LANGUAGE_VALUE)
                .build();
        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Метод формирующий запрос
    //предаем метод сортировки в метод построения URL
    // Передаем в параметрах номер страницы
    public static URL buildURL(int sortBy, int page) {
        //Переменная которую нужно вернуть
        URL result = null;
        String methodOfSort;
        if (sortBy == POPULARITY) {
            methodOfSort = SORT_BY_POPULARITY;
        } else {
            methodOfSort = SORT_BY_TOP_RATED;
        }
        //Преобразуем строку в ссылку к поторой можно прикреплять запросы в сеть
        //appendQueryParameter прикрепляем ключи и параметры к ним
        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(PARAMS_API_KEY, API_KEY)
                .appendQueryParameter(PARAMS_LANGUAGE, LANGUAGE_VALUE)
                .appendQueryParameter(PARAMS_SORT_BY, methodOfSort)
                .appendQueryParameter(PARAMS_MIN_VOTE_COUNT, MIN_VOTE_COUNT_VALUE)
                .appendQueryParameter(PARAMS_PAGE, Integer.toString(page))
                .build();

        // возвращаем тип URL. Преобразуем Uri в URL
        try {
            result = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return result;
    }

    //Загрузка данных.
    // Отличие от класса JSONLoadTask в том, что мы передаем url не напрямую, а через обект Bundle.
    // При обрыве связи с интернетом или повороте экрана загрузка начнется заново
    public static class JSONLoader extends AsyncTaskLoader<JSONObject> {

        private Bundle bundle;

        public JSONLoader(@NonNull Context context, Bundle bundle) {
            super(context);
            this.bundle = bundle;
        }

        // необходимо переопределить метод onStartLoading() и вызвать метод forceLoad() для продолжения загрузки
        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            forceLoad();
        }

        @Nullable
        @Override
        public JSONObject loadInBackground() {
            if (bundle == null) {
                return null;
            }
            String urlAsString = bundle.getString("url");
            URL url = null;
            try {
                url = new URL(urlAsString);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            JSONObject result = null;
            //если urls == null то возвращаем null
            if (url == null) {
                return null;
            }
            // если все нормально то возвращаем:
            HttpURLConnection connection = null;
            //открываем соединение
            try {
                connection = (HttpURLConnection) url.openConnection();
                //Когда соединение открыто, Создаем поток ввода.
                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                //Чтобы читать сразу строками создаем:
                BufferedReader reader = new BufferedReader(inputStreamReader);
                //Чтобы сохранять строки создаем
                StringBuilder builder = new StringBuilder();
                // Начинаем читать данные
                String line = reader.readLine();
                //Пока line != null нужно сохранять полученную строку
                while (line != null) {
                    builder.append(line);
                    //после присоединения линии к билдеру, присваиваем линии значение:
                    line = reader.readLine();
                }
                // После того как мы прочитали все данные, мы переменной result присваиваем значение:
                result = new JSONObject(builder.toString());
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            Log.i("qwertyu", "JSONLoadTask   JSONObject result =  " + result);
            return result;
        }
    }

    //Создаем метод который будет загружать данные из интернета. Для этого нам понадобится класс AsyncTask
    //В качестве параметров принимает URL, данные в процессе выполнения Void, возвращает JSONObject
    private static class JSONLoadTask extends AsyncTask<URL, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(URL... urls) {
            JSONObject result = null;
            //если urls == null то возвращаем null
            if (urls == null || urls.length == 0) {
                return result;
            }
            // если все нормально то возвращаем:
            HttpURLConnection connection = null;
            //открываем соединение
            try {
                connection = (HttpURLConnection) urls[0].openConnection();
                //Когда соединение открыто, Создаем поток ввода.
                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                //Чтобы читать сразу строками создаем:
                BufferedReader reader = new BufferedReader(inputStreamReader);
                //Чтобы сохранять строки создаем
                StringBuilder builder = new StringBuilder();
                // Начинаем читать данные
                String line = reader.readLine();
                //Пока line != null нужно сохранять полученную строку
                while (line != null) {
                    builder.append(line);
                    //после присоединения линии к билдеру, присваиваем линии значение:
                    line = reader.readLine();
                }
                // После того как мы прочитали все данные, мы переменной result присваиваем значение:
                result = new JSONObject(builder.toString());
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            Log.i("qwertyu", "JSONLoadTask   JSONObject result =  " + result);
            return result;
        }
    }

    //Создаем метод который будет получать JSON из сети.
    public static JSONObject getJSONFromNetwork(int sortBy, int page) {
        JSONObject result = null;
        //вызываем метод формирующий запрос
        URL url = buildURL(sortBy, page); //  Получили URL
        Log.i("qwertyu", "url в NetworkUtils = " + url.toString());
        //Создаем объет класса
        try {
            result = new JSONLoadTask().execute(url).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    //Создаем метод который будет получать JSON из сети. Для видео.
    public static JSONObject getJSONForVideos(int id) {
        JSONObject result = null;
        //вызываем метод формирующий запрос
        URL url = buildUrlToVideos(id); //  Получили URL
        Log.i("qwertyu", "url в NetworkUtils = " + url.toString());
        //Создаем объет класса
        try {
            result = new JSONLoadTask().execute(url).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    //Создаем метод который будет получать JSON из сети. Для отзывов.
    public static JSONObject getJSONForReviews(int id) {
        JSONObject result = null;
        //вызываем метод формирующий запрос
        URL url = buildUrlToReviews(id); //  Получили URL
        Log.i("qwertyu", "url в NetworkUtils = " + url.toString());
        //Создаем объет класса
        try {
            result = new JSONLoadTask().execute(url).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }
}