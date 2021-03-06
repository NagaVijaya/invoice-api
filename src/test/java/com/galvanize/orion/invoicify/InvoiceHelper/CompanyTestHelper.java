package com.galvanize.orion.invoicify.InvoiceHelper;

import com.galvanize.orion.invoicify.entities.Company;

public class CompanyTestHelper {
    public static Company getCompanyOne() {
        Company companyOne = Company.builder()
                .name("Company One")
                .build();
        return companyOne;
    }

    public static Company getCompanyTwo() {
        Company companyOne = Company.builder()
                .name("Company Two")
                .build();
        return companyOne;
    }
}
