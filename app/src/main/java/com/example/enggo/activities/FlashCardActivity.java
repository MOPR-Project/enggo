package com.example.enggo.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.enggo.R;
import com.example.enggo.helpers.TranslateRetrofitClient;
import com.example.enggo.helpers.WordStorageManager;
import com.example.enggo.models.WordStorage;
import com.example.enggo.service.TranslateApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FlashCardActivity extends AppCompatActivity {

    private LinearLayout flashCardRoot;
    private TextView tvTitle, tvPhonetic;
    private Button btnContinue, btnCheck;
    private List<WordStorage> favoriteWords;
    private int currentIndex = 0;

    private boolean isShowingVietnamese = false;
    private String originalWord = "";
    private String translatedMeaning = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard);

        flashCardRoot = findViewById(R.id.flashCardRoot);
        tvTitle = findViewById(R.id.tvTitle);
        tvPhonetic = findViewById(R.id.tvPhonetic);
        btnContinue = findViewById(R.id.btnContinue);
        btnCheck = findViewById(R.id.btnCheck);

        favoriteWords = WordStorageManager.getWordFavorite(this);

        if (favoriteWords.isEmpty()) {
            tvTitle.setText("No favorites");
            tvPhonetic.setText("");
            btnCheck.setEnabled(false);
            btnContinue.setEnabled(false);
            return;
        }

        showFlashcard(currentIndex);


        btnContinue.setOnClickListener(v -> {
            currentIndex++;
            if (currentIndex >= favoriteWords.size()) {
                Toast.makeText(this, "Bạn đã hoàn thành tất cả từ!", Toast.LENGTH_SHORT).show();
                currentIndex = 0;
            }
            isShowingVietnamese = false;
            slideCard(() -> showFlashcard(currentIndex));
        });
        btnCheck.setOnClickListener(v -> {
            if (isShowingVietnamese) {
                flipCard(() -> {
                    tvTitle.setText(originalWord);
                    isShowingVietnamese = false;
                });
            } else {
                originalWord = tvTitle.getText().toString();
                TranslateApiService api = TranslateRetrofitClient.getApi();

                api.translate(originalWord).enqueue(new Callback<List<String>>() {
                    @Override
                    public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            translatedMeaning = response.body().get(0);
                            flipCard(() -> {
                                tvTitle.setText(translatedMeaning);
                                isShowingVietnamese = true;
                            });
                        } else {
                            Toast.makeText(FlashCardActivity.this, "Không tìm thấy nghĩa.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<String>> call, Throwable t) {
                        Toast.makeText(FlashCardActivity.this, "Lỗi dịch từ", Toast.LENGTH_SHORT).show();
                        t.printStackTrace();
                    }
                });
            }
        });
    }

    private void showFlashcard(int index) {
        WordStorage word = favoriteWords.get(index);
        tvTitle.setText(word.getWord());
        tvPhonetic.setText(word.getPhonetic());
    }

    private void slideCard(Runnable onUpdate) {
        View cardView = findViewById(R.id.tvTitle).getRootView();

        cardView.animate()
                .translationX(-cardView.getWidth())
                .alpha(1f)
                .setDuration(800)
                .withEndAction(() -> {
                    onUpdate.run();
                    cardView.setTranslationX(cardView.getWidth());
                    cardView.animate()
                            .translationX(0)
                            .alpha(1f)
                            .setDuration(800)
                            .start();
                })
                .start();
    }

    private void flipCard(Runnable onMidFlip) {
        LinearLayout cardView = flashCardRoot;

        ObjectAnimator flipOut = ObjectAnimator.ofFloat(cardView, "rotationY", 0f, 90f);
        flipOut.setDuration(300);

        ObjectAnimator flipIn = ObjectAnimator.ofFloat(cardView, "rotationY", -90f, 0f);
        flipIn.setDuration(300);

        flipOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                onMidFlip.run(); // Thực hiện thay đổi nội dung giữa hiệu ứng
                flipIn.start();  // Tiếp tục quay phần còn lại
            }
        });

        flipOut.start();
    }

}
