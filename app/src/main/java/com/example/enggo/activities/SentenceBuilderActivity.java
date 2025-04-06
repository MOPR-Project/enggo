package com.example.enggo.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.google.android.flexbox.FlexboxLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SentenceBuilderActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private FlexboxLayout flexboxWordContainer;
    private RecyclerView recyclerViewWords;

    private WordAdapter wordAdapter;

    private List<Sentence> sentenceList = new ArrayList<>();
    private int currentIndex = 0;

    private List<String> wordList = new ArrayList<>();
    private FlexboxLayout selectedWordsLayout;
    private List<String> selectedWords = new ArrayList<>();
    private SentenceApiService apiService;
    private ImageButton btnSubmit; // Nút Submit

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sentence_builder);

        flexboxWordContainer = findViewById(R.id.flexboxWordContainer);
        selectedWordsLayout = findViewById(R.id.selectedWordsLayout);
        btnSubmit = findViewById(R.id.btnSubmit);
        progressBar = findViewById(R.id.progressBar);


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
                        progressBar.setMax(sentenceList.size());
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

            progressBar.setProgress(currentIndex);

            wordList.clear();
            wordList.addAll(currentSentence.getWords());
            Collections.shuffle(wordList);

            selectedWords.clear();
            updateSelectedWordsUI();

            setupFlexboxWords();
        } else {
            Intent intent = new Intent(SentenceBuilderActivity.this, CongratulationsActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void setupFlexboxWords() {
        flexboxWordContainer.removeAllViews();

        for (String word : wordList) {
            if (selectedWords.contains(word)) continue;

            Button wordButton = new Button(this);
            wordButton.setText(word);

            // Style
            wordButton.setTextColor(Color.WHITE);
            wordButton.setTextSize(16f);
            wordButton.setPadding(24, 12, 24, 12);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 8, 8, 8);
            wordButton.setLayoutParams(params);

            wordButton.setOnClickListener(v -> {
                selectedWords.add(word);
                updateSelectedWordsUI();
                setupFlexboxWords();
            });

            flexboxWordContainer.addView(wordButton);
        }
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

            // Style button
            wordButton.setTextSize(16);
            wordButton.setTextColor(getResources().getColor(android.R.color.black));
            wordButton.setBackgroundResource(R.drawable.word_button_bg); // custom drawable background
            wordButton.setPadding(30, 16, 30, 16);


            wordButton.setAllCaps(false);
            wordButton.setElevation(0);

            // Margin giữa các nút
            FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(
                    FlexboxLayout.LayoutParams.WRAP_CONTENT,
                    FlexboxLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(12, 16, 12, 16);
            wordButton.setLayoutParams(params);

            wordButton.setOnClickListener(v -> {
                selectedWords.remove(word);
                updateSelectedWordsUI();
                setupFlexboxWords();
            });

            selectedWordsLayout.addView(wordButton);
        }
    }


    private void submitSentence() {
        if (currentIndex >= sentenceList.size()) return;

        Sentence currentSentence = sentenceList.get(currentIndex);

        if (selectedWords.isEmpty()) {
            Toast.makeText(this, "Please select words", Toast.LENGTH_SHORT).show();
            return;
        }

        // compare to correctWords
        List<String> correctWords = currentSentence.getWords();
        if (selectedWords.equals(correctWords)) {
            Toast.makeText(this, "✅ Correct!", Toast.LENGTH_SHORT).show();

            SentenceSubmitRequest request = new SentenceSubmitRequest(currentSentence.getId(), selectedWords);
            apiService.submitSentence(request).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Log.e("API_ERROR", "Failed to submit sentence", t);
                }
            });

            currentIndex++;
            selectedWords.clear();
            showNextSentence();
        } else {
            Toast.makeText(this, "❌ Incorrect! Try again.", Toast.LENGTH_SHORT).show();
        }
    }

}