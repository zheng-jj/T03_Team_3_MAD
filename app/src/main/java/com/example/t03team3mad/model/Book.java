package com.example.t03team3mad.model;

public class Book {
    private String booktitle;
    public String getBooktitle() {
        return booktitle;
    }
    public void setBooktitle(String newName) {
        this.booktitle = newName;
    }

    private String bookauthor;
    public String getBookauthor() {
        return bookauthor;
    }
    public void setBookauthor(String newName) {
        this.bookauthor = newName;
    }

    private String bookabout;
    public String getBookabout() {
        return bookabout;
    }
    public void setBookabout(String newName) {
        this.bookabout = newName;
    }

    private String bookgenre;
    public String getBookgenre() {
        return bookgenre;
    }
    public void setBookgenre(String newName) {
        this.bookgenre = newName;
    }

    private String pdate;
    public String getPdate() {
        return pdate;
    }
    public void setPdate(String newName) {
        this.pdate = newName;
    }

    private String isbn;
    public String getIsbn() {
        return isbn;
    }
    public void setIsbn(String new1Name) {
        this.isbn = new1Name;
    }


    public Book(String title, String author, String about, String genre, String publish, String bisbn) {
        booktitle = title;
        bookauthor = author;
        bookabout = about;
        bookgenre = genre;
        pdate = publish;
        isbn = bisbn;
    }
}
