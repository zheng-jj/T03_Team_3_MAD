package com.T03G3.eLibtheBookManager.model;

public class NLB {
    private String BranchName;
    private Boolean  Availability;
    private String Status;
    private String ItemNo;

    public NLB(String branchName, String itemNo, Boolean avai, String status) {

        BranchName = branchName;
        ItemNo = itemNo;
        Availability = avai;
        Status = status;
    }

    public NLB(){}

    public String getBranchName() {
        return BranchName;
    }

    public void setBranchName(String name) {
        BranchName = name;
    }

    public void setAvailability(Boolean availability) {
        Availability = availability;
    }
    public Boolean getAvailability() {
        return Availability;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String name) {
        Status = name;
    }

    public String getItemNo() {
        return ItemNo;
    }

    public void setItemNo(String name) {
        ItemNo = name;
    }
}
