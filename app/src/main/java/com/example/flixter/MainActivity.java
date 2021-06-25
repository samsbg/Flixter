package com.example.flixter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixter.adapters.MovieAdapter;
import com.example.flixter.databinding.ActivityMainBinding;
import com.example.flixter.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;


public class MainActivity extends AppCompatActivity {

    public static final String NOW_PLAYING_URL = "https://api.themoviedb.org/3/movie/now_playing?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";
    public static final String TAG = "MainActivity";

    public static final String KEY_ITEM_TITLE = "item_title";
    public static final String KEY_ITEM_OVERVIEW = "item_overview";
    public static final String KEY_ITEM_BACKDROP = "item_backdrop";
    public static final String KEY_ITEM_POSTER = "item_poster";
    public static final String KEY_ITEM_GRADE = "item_grade";
    public static final String KEY_ITEM_ID = "item_id";

    public static final int CHECK_DETAIL = 20;

    List<Movie> movies;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        movies = new ArrayList<>();

        MovieAdapter.OnClickListener onClickListener = new MovieAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position) {
                Log.d("MainActivity", "Click at position " + position);
                Intent i = new Intent(MainActivity.this, MovieDetails.class);

                i.putExtra(KEY_ITEM_TITLE, movies.get(position).getTitle());
                i.putExtra(KEY_ITEM_OVERVIEW, movies.get(position).getOverview());
                i.putExtra(KEY_ITEM_BACKDROP, movies.get(position).getBackdropPath());
                i.putExtra(KEY_ITEM_POSTER, movies.get(position).getPosterPath());
                i.putExtra(KEY_ITEM_GRADE, movies.get(position).getGrade());
                i.putExtra(KEY_ITEM_ID, movies.get(position).getId());

                startActivityForResult(i, CHECK_DETAIL);
            }
        };
        MovieAdapter movieAdapter = new MovieAdapter(this, movies, onClickListener);
        binding.rvMovies.setAdapter(movieAdapter);
        binding.rvMovies.setLayoutManager(new LinearLayoutManager(this));

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(NOW_PLAYING_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    Log.i(TAG, "Results: " + results.toString());
                    movies.addAll(Movie.fromJsonArray(results));
                    movieAdapter.notifyDataSetChanged();
                    Log.i(TAG, "Movies: " + movies.size());
                } catch (JSONException e) {
                    Log.e(TAG, "Hit json exception", e);
                }
            }

            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {
                Log.d(TAG, "onFailure");
            }
        });
    }
}