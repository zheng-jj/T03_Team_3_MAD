package com.example.t03team3mad.model;

public class Review {
    private int reviewidu;
    public int getReviewidu() {
        return reviewidu;
    }
    public void setReviewidu(int newReviewidu) {
        this.reviewidu = newReviewidu;
    }

    private int reviewidr;
    public int getReviewidr() {
        return reviewidr;
    }
    public void setReviewidr(int newReviewidr) {
        this.reviewidu = newReviewidr;
    }

    private String reviewtext;
    public String getReviewtext() {
        return reviewtext;
    }
    public void setReviewtext(String newReviewtext) {
        this.reviewtext = newReviewtext;
    }

    private String reviewisbn;
    public String getReviewisbn() {
        return reviewisbn;
    }
    public void setReviewisbn(String newReviewisbn) {
        this.reviewisbn = newReviewisbn;
    }

    public Review (int idu, int idr, String text, String isbn) {
        reviewidu = idu;
        reviewidr = idr;
        reviewtext = text;
        reviewisbn = isbn;
    }
}
