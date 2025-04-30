package com.example.enggo.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.enggo.R;
import com.example.enggo.adapters.FavoriteWordAdapter;
import com.example.enggo.helpers.WordStorageManager;

import java.util.List;

public class FavoriteWordActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FavoriteWordAdapter adapter;
    private List<String> favoriteWords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_word);

        recyclerView = findViewById(R.id.recyclerViewFavorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        favoriteWords = WordStorageManager.getWordFavorite(this);
        Log.d("DictionaryData", "Word: " + favoriteWords);

        adapter = new FavoriteWordAdapter(favoriteWords, this);
        recyclerView.setAdapter(adapter);
    }
}
