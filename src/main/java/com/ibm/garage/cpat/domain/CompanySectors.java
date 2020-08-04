package com.ibm.garage.cpat.domain;


public class CompanySectors {
    
    public String company_id;
    public String company_name;
    public String tckr;
    public String sector_cd;


    public CompanySectors() {

    }

    public CompanySectors(String company_id, String company_name, String tckr, String sector_cd) {
        this.company_id = company_id;
        this.company_name = company_name;
        this.tckr = tckr;
        this.sector_cd = sector_cd;
    }
}