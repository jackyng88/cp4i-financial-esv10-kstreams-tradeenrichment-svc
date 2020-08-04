package com.ibm.garage.cpat.domain;

public class EnrichedMessage {
    
    public String user_id;
    public String stock_symbol;
    public String exchange_id;
    public String trade_type;
    public String date_created;
    public String date_submitted;
    public int quantity;
    public double stock_price;
    public double total_cost;
    public int institution_id;
    public int country_id;
    public boolean compliance_services;
    public boolean technical_validation;
    public boolean schema_validation;
    public boolean business_validation;
    public boolean trade_enrichment;

    public String company_id;
    public String company_name;
    // tckr will be the groupBy value (probably)
    public String tckr;
    public String sector_cd;

    public EnrichedMessage() {

    }

    public EnrichedMessage(FinancialMessage financialMessage, CompanySectors companySectors) {

        this.user_id = financialMessage.user_id;
        this.stock_symbol = financialMessage.stock_symbol;
        this.exchange_id = financialMessage.exchange_id;
        this.trade_type = financialMessage.trade_type;
        this.date_created = financialMessage.date_created;
        this.date_submitted = financialMessage.date_submitted;
        this.quantity = financialMessage.quantity;
        this.stock_price = financialMessage.stock_price;
        this.total_cost = financialMessage.total_cost;
        this.institution_id = financialMessage.institution_id;
        this.country_id = financialMessage.country_id;
        this.compliance_services = financialMessage.compliance_services;
        this.technical_validation = financialMessage.technical_validation;
        this.schema_validation = financialMessage.schema_validation;
        this.business_validation = financialMessage.business_validation;
        this.trade_enrichment = financialMessage.trade_enrichment;

        this.company_id = companySectors.company_id;
        this.company_name = companySectors.company_name;
        this.tckr = companySectors.tckr;
        this.sector_cd = companySectors.sector_cd;
    }
}