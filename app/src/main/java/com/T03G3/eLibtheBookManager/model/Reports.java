package com.T03G3.eLibtheBookManager.model;

public class Reports {
    private String uid;
    private String uname;
    private String aid;
    private String aname;
    private String reason;
    private String specialstring;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getAname() {
        return aname;
    }

    public void setAname(String aname) {
        this.aname = aname;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getSpecialString() {
        return specialstring;
    }

    public void setSpecialString(String specialstring) {
        this.specialstring = specialstring;
    }
    public Reports(){}
}
