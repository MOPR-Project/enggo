package com.example.enggo.models;

import java.io.Serializable;

public class Definition implements Serializable {
    public String definition;
    public String example;

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }
}
