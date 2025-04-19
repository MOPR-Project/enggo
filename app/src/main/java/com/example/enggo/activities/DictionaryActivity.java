package com.example.enggo.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.example.enggo.R;
import com.example.enggo.data.WordResponse;
import com.example.enggo.helpers.WordRetrofitClient;
import com.example.enggo.models.Definition;
import com.example.enggo.models.Meaning;
import com.example.enggo.service.WordApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DictionaryActivity extends AppCompatActivity {
    private WordApiService wordApiService;
    private EditText edtSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dictionary);

        edtSearch = findViewById(R.id.edtSearch);
        wordApiService = WordRetrofitClient.getInstance().create(WordApiService.class);


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

                    String displayWord = result.word;
                    String phonetic = result.phonetic != null ? result.phonetic : (result.phonetics != null && !result.phonetics.isEmpty() ? result.phonetics.get(0).text : "");
                    Meaning meaning = result.meanings.get(0);
                    Definition definition = meaning.definitions.get(0);

                    showWordDetail(displayWord, phonetic, meaning.partOfSpeech, definition.definition, definition.example);
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
    private void showWordDetail(String word, String phonetic, String partOfSpeech, String definition, String example) {
        String message = "Từ: " + word + "\n"
                + "Phiên âm: " + phonetic + "\n"
                + "Loại từ: " + partOfSpeech + "\n"
                + "Định nghĩa: " + definition + "\n"
                + (example != null ? "Ví dụ: " + example : "");

        new AlertDialog.Builder(this)
                .setTitle("Kết quả tra từ")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }


}
