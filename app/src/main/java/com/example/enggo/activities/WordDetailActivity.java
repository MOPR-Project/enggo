package com.example.enggo.activities;

import static com.example.enggo.helpers.WordStorageManager.getWordFavorite;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.enggo.R;
import com.example.enggo.helpers.WordStorageManager;
import com.example.enggo.models.Definition;
import com.example.enggo.models.Meaning;
import com.example.enggo.models.WordStorage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class WordDetailActivity extends AppCompatActivity {

    private TextView tvWord, tvPhonetic;
    private ImageView btnPlayAudio, imgFavorite;
    private LinearLayout tagContainer, meaningsContainer;

    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_detail);

        Mapping();
    }

    private void Mapping() {
        tvWord = findViewById(R.id.tvWord);
        imgFavorite = findViewById(R.id.imgFavorite);
        tvPhonetic = findViewById(R.id.tvPhonetic);
        btnPlayAudio = findViewById(R.id.btnPlayAudio);
        tagContainer = findViewById(R.id.tagContainer);
        meaningsContainer = findViewById(R.id.meaningsContainer);

        String word = getIntent().getStringExtra("word");
        String phonetic = getIntent().getStringExtra("phonetic");
        String audioUrl = getIntent().getStringExtra("audioUrl");
        List<Meaning> meanings = (List<Meaning>) getIntent().getSerializableExtra("meanings");

        tvWord.setText(word);
        tvPhonetic.setText(phonetic);

        renderTags(meanings);
        renderMeanings(meanings);

        btnPlayAudio.setOnClickListener(v -> playAudio(audioUrl));

        isFavorite = WordStorageManager.isWordFavorite(this, word);
        updateFavoriteIcon();
        imgFavorite.setOnClickListener(v -> {
            WordStorage item = new WordStorage(word, phonetic);
            if (isFavorite) {
                WordStorageManager.removeWordFromFavorite(this, word);
                isFavorite = false;
            } else {
                WordStorageManager.addWordToFavorite(this, item);
                isFavorite = true;
            }
            updateFavoriteIcon();
        });
    }
    private void renderTags(List<Meaning> meanings) {
        Set<String> partOfSpeechSet = new LinkedHashSet<>();
        for (Meaning meaning : meanings) {
            partOfSpeechSet.add(meaning.partOfSpeech);
        }

        for (String pos : partOfSpeechSet) {
            TextView tag = new TextView(this);
            tag.setText(pos);
            tag.setPadding(24, 12, 24, 12);
            tag.setTextColor(Color.parseColor("#1976D2"));
            tag.setBackground(ContextCompat.getDrawable(this, R.drawable.tag_background));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMarginEnd(16);
            tag.setLayoutParams(params);
            tagContainer.addView(tag);
        }
    }
    private void renderMeanings(List<Meaning> meanings) {
        meaningsContainer.removeAllViews();
        for (Meaning meaning : meanings) {

            TextView partOfSpeechView = new TextView(this);
            partOfSpeechView.setText(meaning.partOfSpeech);
            partOfSpeechView.setTextSize(18f);
            partOfSpeechView.setTypeface(null, Typeface.BOLD);
            partOfSpeechView.setTextColor(Color.BLACK);
            partOfSpeechView.setPadding(0, 16, 0, 8);
            meaningsContainer.addView(partOfSpeechView);


            List<Definition> definitions = meaning.definitions;
            for (int i = 0; i < Math.min(2, definitions.size()); i++) {
                Definition def = definitions.get(i);

                TextView defView = new TextView(this);
                defView.setText("• " + def.definition);
                defView.setTextColor(Color.DKGRAY);
                defView.setTextSize(16f);
                defView.setPadding(0, 4, 0, 4);
                meaningsContainer.addView(defView);

                if (def.example != null && !def.example.isEmpty()) {
                    TextView exampleView = new TextView(this);
                    exampleView.setText("   → " + def.example);
                    exampleView.setTextColor(Color.GRAY);
                    exampleView.setTypeface(null, Typeface.ITALIC);
                    meaningsContainer.addView(exampleView);
                }
            }
        }
    }
    private void playAudio(String audioUrl) {
        if (audioUrl != null) {
            Log.d("Audio URL", "URL: " + audioUrl);
            MediaPlayer mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(audioUrl);
                mediaPlayer.setOnPreparedListener(mp -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        PlaybackParams params = new PlaybackParams();
                        params.setSpeed(0.75f);
                        mp.setPlaybackParams(params);
                    }
                    mp.start();
                });
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                Toast.makeText(this, "Không thể phát âm thanh", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
    private void updateFavoriteIcon() {
        if (isFavorite) {
            imgFavorite.setImageResource(R.drawable.ic_star);
        } else {
            imgFavorite.setImageResource(R.drawable.ic_star_outline);
        }
    }

}
