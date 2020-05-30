package com.example.t03team3mad.model;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

    private int useridu;

    protected User(Parcel in) {
        useridu = in.readInt();
        username = in.readString();
        userisbn = in.readString();
        userabout = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public int getUseridu() {
        return useridu;
    }
    public void setUseridudu(int newUseridu) {
        this.useridu = newUseridu;
    }

    private String username;
    public String getUsername() {
        return username;
    }
    public void setUsername(String newUsername) {
        this.username = newUsername;
    }

    private String userisbn;
    public String getUserisbn() {
        return userisbn;
    }
    public void setUserisbn(String newUserisbn) {
        this.userisbn = newUserisbn;
    }

    private String userabout;
    public String getUserabout() {
        return userabout;
    }
    public void setUserabout(String newUserabout) {
        this.userabout = newUserabout;
    }

    public User(int idu, String name, String isbn, String about) {
        useridu = idu;
        username = name;
        userisbn = isbn;
        userabout = about;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(useridu);
        dest.writeString(username);
        dest.writeString(userisbn);
        dest.writeString(userabout);
    }
    public boolean equal(Book book) {
        boolean isEqual=false;
        if(book!=null && book instanceof Book) {
            isEqual=(this.userisbn==book.getIsbn());
        }
        return isEqual;
    }

}
