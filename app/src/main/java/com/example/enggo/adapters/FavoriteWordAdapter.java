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

import java.util.List;

public class FavoriteWordAdapter extends RecyclerView.Adapter<FavoriteWordAdapter.WordViewHolder> {

    private List<String> wordList;
    private Context context;

    public FavoriteWordAdapter(List<String> wordList, Context context) {
        this.wordList = wordList;
        this.context = context;
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_word_favorite, parent, false);
        return new WordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
        String word = wordList.get(position);

        holder.tvWord.setText(word);

        holder.btnSpeaker.setOnClickListener(v -> {
            TextToSpeech tts;
        });

        holder.btnDone.setOnClickListener(v -> {
            Toast.makeText(context, "Đã học: " + word, Toast.LENGTH_SHORT).show();
            wordList.remove(position);
            notifyItemRemoved(position);
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
}

