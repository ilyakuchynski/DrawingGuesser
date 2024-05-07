package com.guessdraw.app.handlers;

public class TopicGenerator {
    private static final String[] topics = {
        "apple",
        "banana",
        "carrot",
        "dog",
        "elephant",
        "frog",
        "giraffe",
        "horse",
        "ice cream",
        "jellyfish",
        "kangaroo",
        "lion",
        "monkey",
        "nose",
        "octopus",
        "penguin",
        "quail",
        "rabbit",
        "snake",
        "tiger",
        "umbrella",
        "vulture",
        "whale",
        "xylophone",
        "yak",
        "zebra"
    };
    public static String generateTopic() {
        return topics[(int) (Math.random() * topics.length)];
    }
}
