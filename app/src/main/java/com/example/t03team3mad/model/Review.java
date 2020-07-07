package com.example.t03team3mad.model;

public class Review {
    private int reviewidu;
    public int getReviewidu() {
        return reviewidu;
    }
    public void setReviewidu(int newReviewidu) {
        this.reviewidu = newReviewidu;
    }
    private String uname;
    public String getUname(){
        return uname;
    }
    public void setUName(String newUname){
        this.reviewtext = newUname;
    }
    private String reviewTitle;
    public String getReviewTitle(){
        return reviewTitle;
    }
    public void setReviewTitle(String newReviewTitle){
        this.reviewtext = newReviewTitle;
    }

    private String bookName;
    public String getBookName(){
        return bookName;
    }
    public void setBookName(String newbookName){
        this.bookName = newbookName;
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

    private int reviewpoints;
    public int getReviewpoints() {
        return reviewpoints;
    }
    public void setReviewpoints(int newReviewpoints) {
        this.reviewpoints = newReviewpoints;
    }
    public Review (int idu, int idr,String username,String title, String text, String isbn) {
        reviewidu = idu;
        reviewidr = idr;
        reviewtext = text;
        reviewisbn = isbn;
        uname  =  username;
        reviewTitle  = title;
    }
    public Review (int idu, String username, String text, int points, String isbn) {
        reviewidu = idu;
        reviewtext = text;
        uname  =  username;
        reviewpoints = points;
        reviewisbn = isbn;
    }
}
