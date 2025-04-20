package com.example.enggo.models;

import java.io.Serializable;
import java.util.List;

public class Meaning implements Serializable {
    public String partOfSpeech;
    public List<Definition> definitions;

    public String getPartOfSpeech() {
        return partOfSpeech;
    }

    public void setPartOfSpeech(String partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }

    public List<Definition> getDefinitions() {
        return definitions;
    }

    public void setDefinitions(List<Definition> definitions) {
        this.definitions = definitions;
    }
}
