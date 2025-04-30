package com.example.enggo.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class WordStorageManager {
    private static final String PREFS_NAME = "word_history";
    private static final String KEY_HISTORY = "word_history_list";

    private static final String PREFS_FAVORITE_NAME = "word_history";
    private static final String KEY_FAVORITE = "word_favorite_list";

    public static void addWord(Context context, String word) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> history = new LinkedHashSet<>(prefs.getStringSet(KEY_HISTORY, new LinkedHashSet<>()));
        history.remove(word);
        history.add(word);

        prefs.edit().putStringSet(KEY_HISTORY, history).apply();
    }
    public static void addWordToFavorite(Context context, String word) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FAVORITE_NAME, Context.MODE_PRIVATE);
        Set<String> history = new LinkedHashSet<>(prefs.getStringSet(KEY_FAVORITE, new LinkedHashSet<>()));
        history.remove(word);
        history.add(word);

        prefs.edit().putStringSet(KEY_FAVORITE, history).apply();
    }

    public static List<String> getWordHistory(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> historySet = prefs.getStringSet(KEY_HISTORY, new LinkedHashSet<>());
        return new ArrayList<>(historySet);
    }
    public static List<String> getWordFavorite(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FAVORITE_NAME, Context.MODE_PRIVATE);
        Set<String> historySet = prefs.getStringSet(KEY_FAVORITE, new LinkedHashSet<>());
        return new ArrayList<>(historySet);
    }

    public static void clearWordHistory(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(KEY_HISTORY).apply();
    }
}

