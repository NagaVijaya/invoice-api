package com.galvanize.orion.invoicify.TestHelper;


import com.galvanize.orion.invoicify.entities.Company;

public class CompanyTestHelper {
    public static Company getCompany1() {
        return Company.builder().name("Company1").address("Bucklands hills drive").state("ON").city("Mississauga").zipCode("L6H0K1").build();
    }
}
