package com.T03G3.eLibtheBookManager.model;

public class buyBook {
    private String VendorName;
    private String Condition;
    private String URL;
    private String Price;
    private String Currency;

    public buyBook(String vendorName, String condition, String url, String price, String currency) {
        VendorName = vendorName;
        Condition = condition;
        URL = url;
        Price = price;
        Currency = currency;
    }

    public String getCurrency() {
        return Currency;
    }

    public void setCurrency(String currency) {
        Currency = currency;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getCondition() {
        return Condition;
    }

    public void setCondition(String condition) {
        Condition = condition;
    }

    public String getVendorName() {
        return VendorName;
    }

    public void setVendorName(String vendorName) {
        VendorName = vendorName;
    }
}
