package com.example.enggo.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.example.enggo.R;
import com.example.enggo.data.WordResponse;
import com.example.enggo.helpers.WordHistoryManager;
import com.example.enggo.helpers.WordRetrofitClient;
import com.example.enggo.models.Definition;
import com.example.enggo.models.Meaning;
import com.example.enggo.models.Phonetic;
import com.example.enggo.service.WordApiService;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DictionaryActivity extends AppCompatActivity {
    private WordApiService wordApiService;
    private EditText edtSearch;
    Button btnHistory;


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

        edtSearch = findViewById(R.id.edtSearch);
        btnHistory = findViewById(R.id.btnHistory);

        btnHistory.setOnClickListener(v -> showHistoryDialog());
        edtSearch.setOnEditorActionListener((v, actionId, event) -> {
            String word = v.getText().toString().trim();
            if (!word.isEmpty()) {
                fetchWordData(word);
            }
            return true;
        });

        wordApiService = WordRetrofitClient.getInstance().create(WordApiService.class);
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


                    // Gson gson = new Gson();
                    // Log.d("DictionaryData", "Word: " + displayWord);
                    // Log.d("DictionaryData", "Phonetic: " + phonetic);
                    // Log.d("DictionaryData", "Meaning: " + gson.toJson(meaning));
                    // Log.d("DictionaryData", "Definition: " + gson.toJson(definition));


                    // Shared Referrence
                    WordHistoryManager.addWord(DictionaryActivity.this, displayWord);


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
        List<String> historyList = WordHistoryManager.getWordHistory(this);
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
                    WordHistoryManager.clearWordHistory(this);
                    Toast.makeText(this, "Đã xoá lịch sử", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private void showAllMeanings(String word, String phonetic, List<Meaning> meanings) {

        StringBuilder message = new StringBuilder();
        message.append("Từ: ").append(word).append("\n");
        message.append("Phiên âm: ").append(phonetic).append("\n\n");

        for (Meaning meaning : meanings) {
            message.append("Loại từ: ").append(meaning.partOfSpeech).append("\n");

            List<Definition> definitions = meaning.definitions;
            int limit = Math.min(2, definitions.size());

            for (int i = 0; i < limit; i++) {
                Definition def = definitions.get(i);
                message.append(" - Định nghĩa: ").append(def.definition).append("\n");
                if (def.example != null && !def.example.isEmpty()) {
                    message.append("   Ví dụ: ").append(def.example).append("\n");
                }
            }

            message.append("\n");
        }

        new AlertDialog.Builder(this)
                .setTitle("Kết quả tra từ")
                .setMessage(message.toString())
                .setPositiveButton("OK", null)
                .show();

        if (audioUrl != null) {
            MediaPlayer mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(audioUrl);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}

