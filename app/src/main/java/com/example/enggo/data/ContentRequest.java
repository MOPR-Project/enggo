package com.example.enggo.data;

import java.util.List;

public class ContentRequest {
    public List<Content> contents;

    public ContentRequest(List<Content> contents) {
        this.contents = contents;
    }

    public static class Content {
        public List<Part> parts;
        public String role = "user";

        public Content(List<Part> parts) {
            this.parts = parts;
        }
    }

    public static class Part {
        public String text;

        public Part(String text) {
            this.text = text;
        }
    }
}

