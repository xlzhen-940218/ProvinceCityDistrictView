package com.xlzhen.provincecitydistrictview.bean;

import java.util.List;

public class TownBean {
    private String n;
    private List<String> villages;

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public List<String> getVillages() {
        return villages;
    }

    public void setVillages(List<String> villages) {
        this.villages = villages;
    }
}
