package com.xlzhen.provincecitydistrictview.bean;

import java.util.List;

public class ProvinceBean {
    private String n;
    private List<CityBean> city;

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public List<CityBean> getCity() {
        return city;
    }

    public void setCity(List<CityBean> city) {
        this.city = city;
    }
}
