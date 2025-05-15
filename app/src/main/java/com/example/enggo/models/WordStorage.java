package com.example.enggo.models;

public class WordStorage {
    private String word;
    private String phonetic;

    public WordStorage(String word, String phonetic) {
        this.word = word;
        this.phonetic = phonetic;
    }

    public String getWord() {
        return word;
    }
    public String getPhonetic() {
        return phonetic;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setPhonetic(String phonetic) {
        this.phonetic = phonetic;
    }
}
