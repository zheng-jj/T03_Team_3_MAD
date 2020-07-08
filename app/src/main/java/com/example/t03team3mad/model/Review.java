package com.example.t03team3mad.model;

public class Review {


    private String isbn;
    private String uid;
    private String uname;
    private int vote;
    private String review;
    private String bookName;
    private String rid;
    private String imglink;




    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String issbn) {
        this.isbn = issbn;
    }



    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public int getVote() {
        return vote;
    }

    public void setVote(int vote) {
        this.vote = vote;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getImglink() {
        return imglink;
    }

    public void setImglink(String imglink) {
        this.imglink = imglink;
    }
    public Review(int uid, String name, String review, int points, String userID){}
    public Review(String idu, String idr,String name,String title, String Review, String ISBN){
        uid = idu;
        rid= idr;
        uname = name;
        bookName = title;
        review =Review;
         isbn = ISBN;
    }
    public Review(){}

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
