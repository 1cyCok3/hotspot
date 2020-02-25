package com.mapreduce.hotspot.Model;

import java.util.ArrayList;

public class Author {
    private String name;
    private String email;
    private ArrayList<FieldOfStudy> fieldOfStudies;
    private String hot;
    private ArrayList<Article> articles;

    public Author(String name, String email, ArrayList<FieldOfStudy> fieldOfStudies, String hot, ArrayList<Article> articles) {
        this.name = name;
        this.email = email;
        this.fieldOfStudies = fieldOfStudies;
        this.hot = hot;
        this.articles = articles;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<FieldOfStudy> getFieldOfStudies() {
        return fieldOfStudies;
    }

    public void setFieldOfStudies(ArrayList<FieldOfStudy> fieldOfStudies) {
        this.fieldOfStudies = fieldOfStudies;
    }

    public String getHot() {
        return hot;
    }

    public void setHot(String hot) {
        this.hot = hot;
    }

    public ArrayList<Article> getArticles() {
        return articles;
    }

    public void setArticles(ArrayList<Article> articles) {
        this.articles = articles;
    }
}
