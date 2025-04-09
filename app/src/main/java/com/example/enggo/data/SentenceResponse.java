package com.example.enggo.data;

import com.example.enggo.models.Sentence;
import java.util.List;

public class SentenceResponse {
    private String status;
    private List<Sentence> sentences;

    public String getStatus() {
        return status;
    }

    public List<Sentence> getSentences() {
        return sentences;
    }
}

