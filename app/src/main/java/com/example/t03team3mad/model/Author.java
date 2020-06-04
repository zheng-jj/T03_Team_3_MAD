package com.example.t03team3mad.model;

import android.os.Parcel;
import android.os.Parcelable;
//qh - added parcelable
public class Author implements Parcelable {

    private int authorid;

    protected Author(Parcel in) {
        authorid = in.readInt();
        authorname = in.readString();
        authorabout = in.readString();
    }

    public static final Creator<Author> CREATOR = new Creator<Author>() {
        @Override
        public Author createFromParcel(Parcel in) {
            return new Author(in);
        }

        @Override
        public Author[] newArray(int size) {
            return new Author[size];
        }
    };

    public int getAuthorid() {
        return authorid;
    }
    public void setAuthorid(int newAuthorid) {
        this.authorid = newAuthorid;
    }

    private String authorname;
    public String getAuthorname() {
        return authorname;
    }
    public void setAuthorname(String newAuthorname) {
        this.authorname = newAuthorname;
    }

    private String authorabout;
    public String getAuthorabout() {
        return authorabout;
    }
    public void setAuthorabout(String newAuthorabout) {
        this.authorabout = newAuthorabout;
    }

    public Author(int id, String name, String about){
        authorid = id;
        authorname = name;
        authorabout = about;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(authorid);
        dest.writeString(authorname);
        dest.writeString(authorabout);
    }
}
