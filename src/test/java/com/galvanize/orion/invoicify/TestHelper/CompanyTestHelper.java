package com.galvanize.orion.invoicify.TestHelper;


import com.galvanize.orion.invoicify.entities.Company;

import java.util.UUID;

public class CompanyTestHelper {
    public static Company getCompany1() {
        Company company = new Company();
        company.setAddress("Bucklands hills drive");
        company.setCity("Mississauga");
        company.setState("ON");
        company.setZipCode("L6H0K1");
        company.setName("Company1");
        return company;
    }

    public static Company getExistingCompany1() {
        Company company = new Company();
        company.setAddress("Bucklands hills drive");
        company.setCity("Mississauga");
        company.setState("ON");
        company.setZipCode("L6H0K1");
        company.setName("Company1");
        company.setId(UUID.fromString("4fa30ded-c47c-436a-9616-7e3b36be84b2"));
        return company;
    }
}
