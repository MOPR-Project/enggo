package com.example.enggo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.enggo.R;

import java.util.List;

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.WordViewHolder> {
    private List<String> wordList;
    private OnWordClickListener listener;

    public interface OnWordClickListener {
        void onWordClick(String word);
    }

    public WordAdapter(List<String> wordList, OnWordClickListener listener) {
        this.wordList = wordList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_word, parent, false);
        return new WordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
        String word = wordList.get(position);
        holder.btnWord.setText(word);
        holder.btnWord.setOnClickListener(v -> listener.onWordClick(word));
    }

    @Override
    public int getItemCount() {
        return wordList.size();
    }

    public static class WordViewHolder extends RecyclerView.ViewHolder {
        Button btnWord;

        public WordViewHolder(@NonNull View itemView) {
            super(itemView);
            btnWord = itemView.findViewById(R.id.btnWord);
        }
    }
}
