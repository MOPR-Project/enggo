package com.example.enggo.data;

import com.example.enggo.models.Meaning;
import com.example.enggo.models.Phonetic;

import java.util.List;

public class WordResponse {
    public String word;
    public String phonetic;
    public List<Phonetic> phonetics;
    public List<Meaning> meanings;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getPhonetic() {
        return phonetic;
    }

    public void setPhonetic(String phonetic) {
        this.phonetic = phonetic;
    }

    public List<Phonetic> getPhonetics() {
        return phonetics;
    }

    public void setPhonetics(List<Phonetic> phonetics) {
        this.phonetics = phonetics;
    }

    public List<Meaning> getMeanings() {
        return meanings;
    }

    public void setMeanings(List<Meaning> meanings) {
        this.meanings = meanings;
    }
}
