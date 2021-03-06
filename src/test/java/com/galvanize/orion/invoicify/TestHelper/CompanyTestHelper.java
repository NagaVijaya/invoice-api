package com.galvanize.orion.invoicify.TestHelper;

import com.galvanize.orion.invoicify.dto.SimpleCompany;
import com.galvanize.orion.invoicify.entities.Company;
import com.galvanize.orion.invoicify.entities.Invoice;

import java.util.Arrays;
import java.util.List;
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

    public static Company getCompanyOne() {
        Company companyOne = new Company();
        companyOne.setName("Company One");
        companyOne.setAddress("123 Seasame st");
        companyOne.setState("IL");
        companyOne.setCity("Chicago");
        companyOne.setZipCode("60601");

        return companyOne;
    }

    public static Company getCompanyTwo() {
        Company companyTwo = new Company();
        companyTwo.setName("Company Two");
        companyTwo.setAddress("123 High St");
        companyTwo.setState("OH");
        companyTwo.setCity("Columbus");
        companyTwo.setZipCode("40288");

        return companyTwo;
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
        Company archivedCompany = new Company();
        archivedCompany.setName("Archived Company");
        archivedCompany.setAddress("632 Pitchfork Dr");
        archivedCompany.setState("CO");
        archivedCompany.setCity("Colorado Springs");
        archivedCompany.setZipCode("60622");
        archivedCompany.setArchived(true);
        return archivedCompany;
    }

    public static Company getCompanyWithInvoices() {
        Invoice invoice1 = InvoiceTestHelper.getInvoiceWithOneLineItem();
        Invoice invoice2 = InvoiceTestHelper.getInvoiceWithTwoLineItem();
        List<Invoice> invoiceList = Arrays.asList(invoice1, invoice2);
        Company company = new Company();
        company.setName("Archived Company");
        company.setAddress("632 Pitchfork Dr");
        company.setState("CO");
        company.setCity("Colorado Springs");
        company.setZipCode("60622");
        company.setArchived(true);
        company.setInvoices(invoiceList);
        return company;
    }
}