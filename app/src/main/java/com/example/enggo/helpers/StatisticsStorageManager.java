package com.example.enggo.helpers;

import android.content.Context;
import android.content.SharedPreferences;

public class StatisticsStorageManager {
    private static final String PREFS_NAME = "sentence_complete_count";
    private static final String KEY_COUNT = "completed_count";

    public static void incrementCompletedCount(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int current = prefs.getInt(KEY_COUNT, 0);
        prefs.edit().putInt(KEY_COUNT, current + 1).apply();
    }

    public static int getCompletedCount(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_COUNT, 0);
    }

    public static void resetCompletedCount(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt(KEY_COUNT, 0).apply();
    }
}
