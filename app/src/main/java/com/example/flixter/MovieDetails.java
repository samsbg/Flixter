package com.example.flixter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.RatingBar;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixter.databinding.ActivityMovieDetailsBinding;
import com.example.flixter.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Headers;

public class MovieDetails extends AppCompatActivity {

    private ActivityMovieDetailsBinding binding;

    public static final String KEY_ITEM_TITLE = "item_title";
    public static final int CHECK_DETAIL = 20;

    String idKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMovieDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        getSupportActionBar().setTitle("Movie Details");

        binding.tvTitleD.setText(getIntent().getStringExtra(MainActivity.KEY_ITEM_TITLE));
        binding.tvOverviewD.setText(getIntent().getStringExtra(MainActivity.KEY_ITEM_OVERVIEW));
        binding.ratingBar.setRating((float) getIntent().getDoubleExtra(MainActivity.KEY_ITEM_GRADE, 0));


        final String VIDEOS_URL = "https://api.themoviedb.org/3/movie/" + getIntent().getStringExtra(MainActivity.KEY_ITEM_ID) + "/videos?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed&language=en-US";

        Log.d("MovieDetailsURL", VIDEOS_URL);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(VIDEOS_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                Log.d("MovieDetails", "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    Log.i("MovieDetails", "Results: " + results.toString());
                    for (int j = 0; j < results.length(); j++) {
                        Log.d("MovieDetailsID", results.getJSONObject(j).getString("name"));
                        if(results.getJSONObject(j).getString("name").equals("Official Trailer")) {
                            idKey = results.getJSONObject(j).getString("key");
                            Log.d("MovieDetailsIDKey", idKey);
                        }
                    }
                    Log.d("MovieDetails", "Key: " + idKey);
                } catch (JSONException e) {
                    Log.e("MovieDetails", "Hit json exception", e);
                }
            }

            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {
                Log.d("MovieDetails", "onFailure");
            }
        });

        Glide.with(getApplicationContext())
                .load(getIntent().getStringExtra(MainActivity.KEY_ITEM_BACKDROP))
                .transform(new RoundedCornersTransformation(50, 10))
                .placeholder(R.drawable.backdrop_placeholder)
                .into(binding.ivPosterD);

        binding.ivPosterD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ClickListener", "Video");
                Intent k = new Intent(MovieDetails.this, MovieTrailerActivity.class);
                k.putExtra(KEY_ITEM_TITLE, idKey);

                startActivityForResult(k, CHECK_DETAIL);
            }
        });

    }
}