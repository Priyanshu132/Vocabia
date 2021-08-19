package com.example.vocabia;

public class perma_word {


    private String last_meaning;
    private String last_word;
    private String last_example;
    private String newWord;
    private String meaning;

    public String getNewWord() {
        return newWord;
    }

    public void setNewWord(String newWord) {
        this.newWord = newWord;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    private String example;

    public String getLast_meaning() {
        return last_meaning;
    }

    public void setLast_meaning(String last_meaning) {
        this.last_meaning = last_meaning;
    }

    public String getLast_word() {
        return last_word;
    }

    public void setLast_word(String last_word) {
        this.last_word = last_word;
    }

    public String getLast_example() {
        return last_example;
    }

    public void setLast_example(String last_example) {
        this.last_example = last_example;
    }
}
