package com.galvanize.orion.invoicify.service;

import com.galvanize.orion.invoicify.TestHelper.CompanyTestHelper;
import com.galvanize.orion.invoicify.entities.Company;
import com.galvanize.orion.invoicify.exception.DuplicateCompanyException;
import com.galvanize.orion.invoicify.exception.InvoiceNotFoundException;
import com.galvanize.orion.invoicify.repository.CompanyRepository;
import com.galvanize.orion.invoicify.utilities.Constants;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;

import javax.transaction.Transactional;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
public class CompanyServiceTest {

    @MockBean
    private CompanyRepository companyRepository;


    @Test
    public void test_addCompany() throws DuplicateCompanyException{

        CompanyService companyService = new CompanyService(companyRepository);
        Company company = CompanyTestHelper.getCompany1();
        Company createdCompany = CompanyTestHelper.getCompany1();
        createdCompany.setId(UUID.fromString("4fa30ded-c47c-436a-9616-7e3b36be84b2"));
        when(companyRepository.save(any())).thenReturn(createdCompany);
        Company expectedCompany = companyService.addCompany(company);
        assertEquals(expectedCompany.getId(), createdCompany.getId());
        assertEquals(expectedCompany.getAddress(), createdCompany.getAddress());
        assertEquals(expectedCompany.getState(), createdCompany.getState());
        assertEquals(expectedCompany.getCity(), createdCompany.getCity());
        assertEquals(expectedCompany.getName(), createdCompany.getName());
        assertEquals(expectedCompany.getZipCode(), createdCompany.getZipCode());

        verify(companyRepository, times(1)).save(any());
    }

    @Test
    public void test_addDuplicateCompany() throws DuplicateCompanyException{

        CompanyService companyService = new CompanyService(companyRepository);
        Company company = CompanyTestHelper.getCompany1();
        when(companyRepository.save(any())).thenThrow(DataIntegrityViolationException.class);
        Exception exception = assertThrows(DuplicateCompanyException.class, () -> {
            companyService.addCompany(company);
        });
        String actualMessage = exception.getMessage();
        assertEquals(Constants.DUPLICATE_COMPANY_MESSAGE, actualMessage);
    }
}
