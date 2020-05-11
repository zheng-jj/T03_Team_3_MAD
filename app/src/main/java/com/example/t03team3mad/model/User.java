package com.example.t03team3mad.model;

public class User {

    private int useridu;
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
        this.username = newUserisbn;
    }

    private String userabout;
    public String getUserabout() {
        return userabout;
    }
    public void setUserabout(String newUserabout) {
        this.username = newUserabout;
    }

    public User(int idu, String name, String isbn, String about) {
        useridu = idu;
        username = name;
        userisbn = isbn;
        userabout = about;
    }
}
