package com.mapreduce.hotspot.Model;

import java.math.BigInteger;
import java.util.ArrayList;

public class Article {
    private String title;
    private ArrayList<Author> authors;
    private String venue;
    private BigInteger year;
    private ArrayList<String> abstractWords;
    private ArrayList<String> references;
    private ArrayList<String> referencedBy;
    private BigInteger citationNumber;
    private ArrayList<FieldOfStudy> fieldOfStudy;
    private String doi;
    private double hot;

    public Article(String title, ArrayList<Author> authors, String venue, BigInteger year, BigInteger citationNumber, double hot) {
        this.title = title;
        this.authors = authors;
        this.venue = venue;
        this.year = year;
        this.citationNumber = citationNumber;
        this.hot = hot;
    }

    public Article(String title, ArrayList<Author> authors, String venue, BigInteger year, ArrayList<String> abstractWords, ArrayList<String> references, ArrayList<String> referencedBy, BigInteger citationNumber, ArrayList<FieldOfStudy> fieldOfStudy, String doi, double hot) {
        this.title = title;
        this.authors = authors;
        this.venue = venue;
        this.year = year;
        this.abstractWords = abstractWords;
        this.references = references;
        this.referencedBy = referencedBy;
        this.citationNumber = citationNumber;
        this.fieldOfStudy = fieldOfStudy;
        this.doi = doi;
        this.hot = hot;
    }

    public double getHot() {
        return hot;
    }

    public void setHot(double hot) {
        this.hot = hot;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(ArrayList<Author> authors) {
        this.authors = authors;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public BigInteger getYear() {
        return year;
    }

    public void setYear(BigInteger year) {
        this.year = year;
    }

    public ArrayList<String> getAbstractWords() {
        return abstractWords;
    }

    public void setAbstractWords(ArrayList<String> abstractWords) {
        this.abstractWords = abstractWords;
    }

    public ArrayList<String> getReferences() {
        return references;
    }

    public void setReferences(ArrayList<String> references) {
        this.references = references;
    }

    public ArrayList<String> getReferencedBy() {
        return referencedBy;
    }

    public void setReferencedBy(ArrayList<String> referencedBy) {
        this.referencedBy = referencedBy;
    }

    public BigInteger getCitationNumber() {
        return citationNumber;
    }

    public void setCitationNumber(BigInteger citationNumber) {
        this.citationNumber = citationNumber;
    }

    public ArrayList<FieldOfStudy> getFieldOfStudy() {
        return fieldOfStudy;
    }

    public void setFieldOfStudy(ArrayList<FieldOfStudy> fieldOfStudy) {
        this.fieldOfStudy = fieldOfStudy;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }
}
