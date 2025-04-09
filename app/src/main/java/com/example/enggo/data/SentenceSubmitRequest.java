package com.example.enggo.data;

import java.util.List;

public class SentenceSubmitRequest {
    private Long sentenceId;
    private List<String> userWords;

    public SentenceSubmitRequest(Long sentenceId, List<String> userWords) {
        this.sentenceId = sentenceId;
        this.userWords = userWords;
    }

    public Long getSentenceId() { return sentenceId; }
    public List<String> getUserWords() { return userWords; }
}

