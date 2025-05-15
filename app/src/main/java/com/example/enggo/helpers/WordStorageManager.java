package com.example.enggo.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.enggo.models.WordStorage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class WordStorageManager {
    private static final String PREFS_NAME = "word_history";
    private static final String KEY_HISTORY = "word_history_list";

    private static final String PREFS_FAVORITE_NAME = "word_favorite";
    private static final String KEY_FAVORITE = "word_favorite_list";

    private static Gson gson = new Gson();

    public static void addWord(Context context, String word) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> history = new LinkedHashSet<>(prefs.getStringSet(KEY_HISTORY, new LinkedHashSet<>()));
        history.remove(word);
        history.add(word);

        prefs.edit().putStringSet(KEY_HISTORY, history).apply();
    }
    public static List<String> getWordHistory(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> historySet = prefs.getStringSet(KEY_HISTORY, new LinkedHashSet<>());
        return new ArrayList<>(historySet);
    }
    public static void clearWordHistory(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(KEY_HISTORY).apply();
    }


    public static void addWordToFavorite(Context context, WordStorage item) {
        List<WordStorage> list = getWordFavorite(context);
        for (WordStorage w : list) {
            if (w.getWord().equals(item.getWord())) return;
        }
        list.add(item);
        saveWordFavorite(context, list);
    }
    public static List<WordStorage> getWordFavorite(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FAVORITE_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_FAVORITE, "[]");
        Type type = new TypeToken<List<WordStorage>>() {}.getType();
        return gson.fromJson(json, type);
    }
    public static boolean isWordFavorite(Context context, String word) {
        List<WordStorage> list = getWordFavorite(context);
        for (WordStorage w : list) {
            if (w.getWord().equalsIgnoreCase(word)) {
                return true;
            }
        }
        return false;
    }
    private static void saveWordFavorite(Context context, List<WordStorage> list) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FAVORITE_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_FAVORITE, gson.toJson(list)).apply();
    }
    public static void removeWordFromFavorite(Context context, String word) {
        List<WordStorage> list = getWordFavorite(context);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getWord().equals(word)) {
                list.remove(i);
                break;
            }
        }
        saveWordFavorite(context, list);
    }




}

