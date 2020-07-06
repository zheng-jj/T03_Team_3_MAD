package com.example.t03team3mad.model;

public class SearchClass {
    private String searchName;
    public String getSearchName() {
        return searchName;
    }
      public void setSearchName(String newsearchName) {
        this.searchName = newsearchName;
    }

    private String searchDes;
    public String getSearchDes() {
        return searchDes;
    }
    public void setSearchDes(String newsearchDes) {
        this.searchDes = newsearchDes;
    }

    private String searchClass;
    public String getSearchClass() {
        return searchClass;
    }
    public void setSearchClass(String newsearchClass) {
        this.searchClass = newsearchClass;
    }

    private String id;
    public String getId() {
        return id;
    }
    public void setId(String newid) {
        this.id = newid;
    }

    public SearchClass (String name, String des, String searchclass, String newid){
        searchName = name;
        searchDes = des;
        searchClass = searchclass;
        id = newid;

    }
}
