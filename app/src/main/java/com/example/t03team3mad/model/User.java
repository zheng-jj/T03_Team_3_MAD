package com.example.t03team3mad.model;

import android.os.Parcel;
import android.os.Parcelable;
//qh - added parcelable
public class User implements Parcelable {

    private int idu;

    protected User(Parcel in) {
        idu = in.readInt();
        name = in.readString();
        isbn = in.readString();
        desc = in.readString();
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
        return idu;
    }
    public void setUseridu(int newUseridu) {
        this.idu = newUseridu;
    }

    private String name;
    public String getUsername() {
        return name;
    }
    public void setUsername(String newUsername) {
        this.name = newUsername;
    }

    private String isbn;
    public String getUserisbn() {
        return isbn;
    }
    public void setUserisbn(String newUserisbn) {
        this.isbn = newUserisbn;
    }

    private String desc;
    public String getUserabout() {
        return desc;
    }
    public void setUserabout(String newUserabout) {
        this.desc = newUserabout;
    }
    public User(){}
    public User(int Idu, String Name, String Isbn, String about) {
        idu = Idu;
        name = Name;
        isbn = Isbn;
        desc = about;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idu);
        dest.writeString(name);
        dest.writeString(isbn);
        dest.writeString(desc);
    }
    public boolean equal(Book book) {
        boolean isEqual=false;
        if(book!=null && book instanceof Book) {
            isEqual=(this.isbn==book.getIsbn());
        }
        return isEqual;
    }
    private String followingstring;
    public String getfollowingstring() {
        return followingstring;
    }
    public void setfollowingstring(String followingstring) {
        this.followingstring = followingstring;
    }


}
