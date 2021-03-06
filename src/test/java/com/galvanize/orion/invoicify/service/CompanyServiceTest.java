package com.galvanize.orion.invoicify.service;

import com.galvanize.orion.invoicify.entities.Company;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    CompanyService companyService;

    @Test
    public void getAllCompaniesTest_withZeroCompanies() {
        companyService = new CompanyService();
        List<Company> result = companyService.getAllCompanies();
        assertEquals(0, result.size());
    }
}