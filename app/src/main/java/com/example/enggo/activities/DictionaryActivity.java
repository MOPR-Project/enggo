package com.example.enggo.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.example.enggo.R;
import com.example.enggo.data.WordResponse;
import com.example.enggo.helpers.WordStorageManager;
import com.example.enggo.helpers.WordRetrofitClient;
import com.example.enggo.models.Definition;
import com.example.enggo.models.Meaning;
import com.example.enggo.models.Phonetic;
import com.example.enggo.service.WordApiService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DictionaryActivity extends AppCompatActivity {
    private WordApiService wordApiService;
    private EditText edtSearch;
    LinearLayout btnHistory;
    LinearLayout btnFavorite;

    private String displayWord;
    private String phonetic;
    private String audioUrl;
    private Meaning meaning;
    private Definition definition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dictionary);

        Mapping();
        wordApiService = WordRetrofitClient.getInstance().create(WordApiService.class);
    }
    private void Mapping()  {
        edtSearch = findViewById(R.id.edtSearch);
        btnHistory = findViewById(R.id.btnHistory);
        btnFavorite = findViewById(R.id.btnFavorite);

        btnHistory.setOnClickListener(v -> showHistoryDialog());
        btnFavorite.setOnClickListener(v -> showFavoriteWords());

        edtSearch.setOnEditorActionListener((v, actionId, event) -> {
            String word = v.getText().toString().trim();
            if (!word.isEmpty()) {
                fetchWordData(word);
            }
            return true;
        });
    }
    private void fetchWordData(String word) {
        Call<List<WordResponse>> call = wordApiService.getWord(word);

        call.enqueue(new Callback<List<WordResponse>>() {
            @Override
            public void onResponse(Call<List<WordResponse>> call, Response<List<WordResponse>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    WordResponse result = response.body().get(0);

                    //word
                    displayWord = result.word;
                    //phonetic
                    phonetic = result.phonetic != null
                            ? result.phonetic
                            : (result.phonetics != null && !result.phonetics.isEmpty() ? result.phonetics.get(0).text : "");
                    //audioUrl
                    if (result.phonetics != null) {
                        for (Phonetic p : result.phonetics) {
                            if (p.audio != null && !p.audio.isEmpty()) {
                                audioUrl = p.audio.startsWith("https:") ? p.audio : "https:" + p.audio;
                                break;
                            }
                        }
                    }
                    //meaning
                    meaning = result.meanings.get(0);
                    //definition
                    Definition definition = meaning.definitions.get(0);

                    // Shared Referrence
                    WordStorageManager.addWord(DictionaryActivity.this, displayWord);


                    //Open WordDetailActivity
                    Intent intent = new Intent(DictionaryActivity.this, WordDetailActivity.class);
                    intent.putExtra("word", displayWord);
                    intent.putExtra("phonetic", phonetic);
                    intent.putExtra("audioUrl", audioUrl);
                    intent.putExtra("meanings", new ArrayList<>(result.meanings));
                    startActivity(intent);

                } else {
                    Toast.makeText(DictionaryActivity.this, "Không tìm thấy từ!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<WordResponse>> call, Throwable t) {
                Toast.makeText(DictionaryActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showHistoryDialog() {
        List<String> historyList = WordStorageManager.getWordHistory(this);
        if (historyList.isEmpty()) {
            Toast.makeText(this, "Chưa có từ nào được tra.", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] historyArray = historyList.toArray(new String[0]);
        new AlertDialog.Builder(this)
                .setTitle("Từ đã tra")
                .setItems(historyArray, (dialog, which) -> {
                    String selectedWord = historyArray[which];
                    edtSearch.setText(selectedWord);
                    fetchWordData(selectedWord);
                })
                .setNegativeButton("Đóng", null)
                .setPositiveButton("Xoá lịch sử", (dialog, which) -> {
                    WordStorageManager.clearWordHistory(this);
                    Toast.makeText(this, "Đã xoá lịch sử", Toast.LENGTH_SHORT).show();
                })
                .show();
    }
    private void showFavoriteWords() {
        Intent intent = new Intent(DictionaryActivity.this, FavoriteWordActivity.class);
        startActivity(intent);
    }

}

