package com.example.enggo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.enggo.R;

public class SentenceLevelActivity extends AppCompatActivity {

    private LinearLayout beginnerCard, elementaryCard, intermediateCard, proficientCard;
    private TextView tvLearnedWords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sentence_level);

        beginnerCard = findViewById(R.id.beginnerCard);
        elementaryCard = findViewById(R.id.elementaryCard);
        intermediateCard = findViewById(R.id.intermediateCard);
        proficientCard = findViewById(R.id.proficientCard);
        tvLearnedWords = findViewById(R.id.tvLearnedWords);

        int learnedSentences = 25; // Giá trị mẫu, sau này lấy từ database
        tvLearnedWords.setText(learnedSentences + " sentences 👏");

        beginnerCard.setOnClickListener(v -> openSentenceBuilderWithLevel(1));
        elementaryCard.setOnClickListener(v -> openSentenceBuilderWithLevel(2));
        intermediateCard.setOnClickListener(v -> openSentenceBuilderWithLevel(3));
        proficientCard.setOnClickListener(v -> openSentenceBuilderWithLevel(4));
    }

    private void openSentenceBuilderWithLevel(int level) {
        Intent intent = new Intent(this, SentenceBuilderActivity.class);
        intent.putExtra("level", level);
        startActivity(intent);
    }
}
