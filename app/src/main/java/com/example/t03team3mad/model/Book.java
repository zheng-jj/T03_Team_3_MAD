package com.example.t03team3mad.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Book implements Parcelable {
    private String booktitle;

    protected Book(Parcel in) {
        booktitle = in.readString();
        bookauthor = in.readString();
        bookabout = in.readString();
        bookgenre = in.readString();
        pdate = in.readString();
        isbn = in.readString();
        nullbook = in.readString();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

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

    private String nullbook;
    public String getNullbook() {
        return isbn;
    }
    public void setNullbook(String new1Name) {
        this.nullbook = new1Name;
    }


    public Book(String title, String author, String about, String genre, String publish, String bisbn) {
        booktitle = title;
        bookauthor = author;
        bookabout = about;
        bookgenre = genre;
        pdate = publish;
        isbn = bisbn;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(booktitle);
        dest.writeString(bookauthor);
        dest.writeString(bookabout);
        dest.writeString(bookgenre);
        dest.writeString(pdate);
        dest.writeString(isbn);
        dest.writeString(nullbook);
    }
    public String ToString(){
        return booktitle+isbn;
    }
}
