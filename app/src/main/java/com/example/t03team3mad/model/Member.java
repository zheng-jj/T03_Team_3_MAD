package com.example.t03team3mad.model;

public class Member {
    private String Name;
    private String Email;
    private String Androidid;
    private String ID;
    private String password;
    private String phonenumber;
    private Boolean banned;

    public Member(){}

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String Password) {
        password = Password;
    }

    public String getDeviceID() {
        return Androidid;
    }

    public void setDeviceID(String androidid) {
        Androidid = androidid;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String Phonenumber) {
        phonenumber = Phonenumber;
    }
    public String getBanned() {
        return String.valueOf(banned);
    }

    public void setBanned(boolean Banned) {
        banned = Banned;
    }
}


