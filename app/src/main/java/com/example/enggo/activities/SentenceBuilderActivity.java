package com.example.enggo.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.enggo.R;
import com.example.enggo.adapters.WordAdapter;
import com.example.enggo.data.ApiResponse;
import com.example.enggo.data.SentenceResponse;
import com.example.enggo.data.SentenceSubmitRequest;
import com.example.enggo.helpers.RetrofitClient;
import com.example.enggo.models.Sentence;
import com.example.enggo.service.SentenceApiService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SentenceBuilderActivity extends AppCompatActivity {
    private RecyclerView recyclerViewWords;
    private WordAdapter wordAdapter;

    private List<Sentence> sentenceList = new ArrayList<>();
    private int currentIndex = 0;

    private List<String> wordList = new ArrayList<>();
    private LinearLayout selectedWordsLayout;
    private List<String> selectedWords = new ArrayList<>();
    private SentenceApiService apiService;
    private ImageButton btnSubmit; // Nút Submit

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sentence_builder);

        recyclerViewWords = findViewById(R.id.recyclerViewWords);
        selectedWordsLayout = findViewById(R.id.selectedWordsLayout);
        btnSubmit = findViewById(R.id.btnSubmit);

        apiService = RetrofitClient.getInstance().create(SentenceApiService.class);

        fetchSentences(1);

        btnSubmit.setOnClickListener(v -> submitSentence());
    }

    private void fetchSentences(int level) {
        apiService.getSentences(level).enqueue(new Callback<SentenceResponse>() {
            @Override
            public void onResponse(Call<SentenceResponse> call, Response<SentenceResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    sentenceList = response.body().getSentences();
                    if (!sentenceList.isEmpty()) {
                        showNextSentence();
                    }
                } else {
                    Toast.makeText(SentenceBuilderActivity.this, "Failed to load sentences", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SentenceResponse> call, Throwable t) {
                Log.e("API_ERROR", "Failed to fetch sentences", t);
            }
        });
    }

    private void showNextSentence() {
        if (currentIndex < sentenceList.size()) {
            Sentence currentSentence = sentenceList.get(currentIndex);
            wordList.clear();
            wordList.addAll(currentSentence.getWords());
            Collections.shuffle(wordList);

            selectedWords.clear(); // Reset danh sách từ đã chọn
            updateSelectedWordsUI();

            setupRecyclerView();
        } else {
            Toast.makeText(this, "You've completed all sentences!", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupRecyclerView() {
        wordAdapter = new WordAdapter(wordList, this::onWordSelected);
        recyclerViewWords.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerViewWords.setAdapter(wordAdapter);
    }

    private void onWordSelected(String word) {
        selectedWords.add(word);
        updateSelectedWordsUI();
    }

    private void updateSelectedWordsUI() {
        selectedWordsLayout.removeAllViews();
        for (String word : selectedWords) {
            Button wordButton = new Button(this);
            wordButton.setText(word);
            wordButton.setOnClickListener(v -> {
                selectedWords.remove(word);
                updateSelectedWordsUI();
            });
            selectedWordsLayout.addView(wordButton);
        }
    }

    private void submitSentence() {
        if (currentIndex >= sentenceList.size()) return;

        Sentence currentSentence = sentenceList.get(currentIndex);

        // Kiểm tra xem người dùng đã chọn từ hay chưa
        if (selectedWords.isEmpty()) {
            Toast.makeText(this, "Please select words", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo request body
        SentenceSubmitRequest request = new SentenceSubmitRequest(currentSentence.getId(), selectedWords);

        // Gửi request
        apiService.submitSentence(request).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SentenceBuilderActivity.this, "Correct!", Toast.LENGTH_SHORT).show();
                    currentIndex++; // Chuyển sang câu tiếp theo
                    showNextSentence();
                } else {
                    Toast.makeText(SentenceBuilderActivity.this, "Try again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("API_ERROR", "Failed to submit sentence", t);
                Toast.makeText(SentenceBuilderActivity.this, "Submission failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}