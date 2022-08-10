package com.xlzhen.provincecitydistrictview.bean;

import java.util.List;

public class CityBean {
    private String n;
    private List<DistrictBean> district;

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public List<DistrictBean> getDistrict() {
        return district;
    }

    public void setDistrict(List<DistrictBean> district) {
        this.district = district;
    }
}
