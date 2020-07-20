package eLib_theBookManager.model;

public class notifications {
    private String notiname;
    private Boolean  bool;
    private String sharedpreferencename;
    private String notitype;

    public notifications(String noti, Boolean boo, String sharedname,String type) {
        notiname = noti;
        bool = boo;
        sharedpreferencename = sharedname;
        notitype = type;
    }

    public notifications(){}

    public String getNotiname() {
        return notiname;
    }

    public void setNotiname(String name) {
        notiname = name;
    }

    public String getNotitype() {
        return notitype;
    }

    public void setNotitype(String name) {
        notitype = name;
    }

    public void setBool(Boolean boo) {
        bool = boo;
    }

    public Boolean getBool() {
        return bool;
    }


    public String getSharedpreferencename() {
        return sharedpreferencename;
    }

    public void setSharedpreferencename(String name) {
        sharedpreferencename = name;
    }

}
