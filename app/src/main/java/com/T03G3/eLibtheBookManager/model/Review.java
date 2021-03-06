package com.T03G3.eLibtheBookManager.model;

public class Review {
    private int reviewidu;

    public Review(String idus, String idrs, String uname, String title, String review, String isbn) {
    }

    public Review(int uid, String review,String title,String isbn) {
        reviewidu = uid;
        reviewtext = review;

        reviewTitle = title;
        reviewisbn = isbn;

    }

    public Review(int uid, String review,String title,String isbn, int rid) {
        reviewidu = uid;
        reviewtext = review;
        reviewidr = rid;
        reviewTitle = title;
        reviewisbn = isbn;

    }

    public Review() {

    }

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

    private String imglink;
    public String getimglink() {
        return imglink;
    }
    public void setImglink(String imglink) {
        this.imglink = imglink;
    }

    private String specialstring;
    public String getSpecialstring() {
        return specialstring;
    }
    public void setSpecialstring(String special) {
        this.specialstring = special;
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
    public Review (int idu, int idr, String username, String text, int points, String isbn) {
        reviewidr = idr;
        reviewidu = idu;
        reviewtext = text;
        uname  =  username;
        reviewpoints = points;
        reviewisbn = isbn;
    }
    public Review (int idu,String username, String text, int points, String isbn) {

        reviewidu = idu;
        reviewtext = text;
        uname  =  username;
        reviewpoints = points;
        reviewisbn = isbn;
    }

}

