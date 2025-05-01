package com.example.enggo.data;

import java.util.List;

public class ContentResponse {
    public List<Candidate> candidates;

    public static class Candidate {
        public Content content;

        public static class Content {
            public List<Part> parts;
            public String role;
        }

        public static class Part {
            public String text;
        }
    }
}

