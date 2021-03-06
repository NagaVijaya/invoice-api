package com.galvanize.orion.invoicify.TestHelper;


import com.galvanize.orion.invoicify.entities.Company;

import java.util.UUID;

public class CompanyTestHelper {
    public static Company getCompany1() {
        return Company.builder().name("Company1").address("Bucklands hills drive").state("ON").city("Mississauga").zipCode("L6H0K1").build();
    }

    public static Company getExistingCompany1() {
        return Company.builder().name("Company1").id(UUID.fromString("4fa30ded-c47c-436a-9616-7e3b36be84b2")).address("Bucklands hills drive").state("ON").city("Mississauga").zipCode("L6H0K1").build();
    }
}
