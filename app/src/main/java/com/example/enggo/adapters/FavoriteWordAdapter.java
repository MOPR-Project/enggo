package com.example.enggo.adapters;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.enggo.R;
import com.example.enggo.helpers.WordStorageManager;
import com.example.enggo.models.WordStorage;

import java.util.List;
import java.util.Locale;

public class FavoriteWordAdapter extends RecyclerView.Adapter<FavoriteWordAdapter.WordViewHolder> {

    private List<WordStorage> wordList;
    private Context context;
    private TextToSpeech tts;

    public FavoriteWordAdapter(List<WordStorage> wordList, Context context) {
        this.wordList = wordList;
        this.context = context;

        // Initialize TextToSpeech
        tts = new TextToSpeech(context, status -> {
            if (status != TextToSpeech.ERROR) {
                tts.setLanguage(Locale.US);
            }
        });
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_word_favorite, parent, false);
        return new WordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
        WordStorage item = wordList.get(position);

        holder.tvWord.setText(item.getWord());
        holder.tvPhonetic.setText(item.getPhonetic());

        holder.btnSpeaker.setOnClickListener(v -> {
            if (tts != null) {
                tts.speak(item.getWord(), TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });

        holder.btnDone.setOnClickListener(v -> {
            Toast.makeText(context, "Đã học: " + item.getWord(), Toast.LENGTH_SHORT).show();

            // 1. Xoá khỏi SharedPreferences
            WordStorageManager.removeWordFromFavorite(context, item.getWord());

            // 2. Xoá khỏi danh sách hiển thị
            wordList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, wordList.size());
        });


    }

    @Override
    public int getItemCount() {
        return wordList.size();
    }

    static class WordViewHolder extends RecyclerView.ViewHolder {

        TextView tvWord, tvPhonetic;
        ImageButton btnSpeaker, btnDone;

        public WordViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWord = itemView.findViewById(R.id.tvWord);
            tvPhonetic = itemView.findViewById(R.id.tvPhonetic);
            btnSpeaker = itemView.findViewById(R.id.btnSpeaker);
            btnDone = itemView.findViewById(R.id.btnDone);
        }
    }

    // Don't forget to shut down TTS
    public void shutdown() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }
}
