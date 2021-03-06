package com.galvanize.orion.invoicify.TestHelper;

import com.galvanize.orion.invoicify.dto.SimpleCompany;
import com.galvanize.orion.invoicify.entities.Company;

public class CompanyTestHelper {

    public static Company getCompany1() {
        return Company.builder()
                .name("Company1")
                .address("Bucklands hills drive")
                .state("ON")
                .city("Mississauga")
                .zipCode("L6H0K1")
                .build();
    }

    public static Company getCompanyOne() {
        Company companyOne = Company.builder()
                .name("Company One")
                .address("123 Seasame st")
                .state("IL")
                .city("Chicago")
                .zipCode("60601")
                .build();
        return companyOne;
    }

    public static Company getCompanyTwo() {
        Company companyOne = Company.builder()
                .name("Company Two")
                .address("123 High St")
                .state("OH")
                .city("Columbus")
                .zipCode("40288")
                .build();
        return companyOne;
    }

    public static SimpleCompany getSimpleCompanyOne() {
        SimpleCompany companyOne = SimpleCompany.builder()
                .name("Company One")
                .state("IL")
                .city("Chicago")
                .build();
        return companyOne;
    }

    public static SimpleCompany getSimpleCompanyTwo() {
        SimpleCompany companyTwo = SimpleCompany.builder()
                .name("Company Two")
                .state("OH")
                .city("Columbus")
                .build();
        return companyTwo;
    }

    public static Company getArchivedCompany() {
        Company archivedCompany = Company.builder()
                .name("Archived Company")
                .address("632 Pitchfork Dr")
                .state("CO")
                .city("Colorado Springs")
                .zipCode("60622")
                .archived(true)
                .build();
        return archivedCompany;
    }
}