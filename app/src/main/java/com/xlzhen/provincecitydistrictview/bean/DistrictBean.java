package com.xlzhen.provincecitydistrictview.bean;

import java.util.List;

public class DistrictBean {
    private String n;
    private List<TownBean> town;

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public List<TownBean> getTown() {
        return town;
    }

    public void setTown(List<TownBean> town) {
        this.town = town;
    }
}
