package com.example.t03team3mad.model;

public class Author {

    private int authorid;
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
}
