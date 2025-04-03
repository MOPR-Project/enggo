package com.example.enggo.models;

import java.util.List;

public class Sentence {
    private Long id;
    private Long level;
    private String sentences;
    private List<String> words;


    public Long getId() {
        return id;
    }

    public Long getLevel() {
        return level;
    }

    public String getSentences() {
        return sentences;
    }

    public List<String> getWords() {
        return words;
    }
}

