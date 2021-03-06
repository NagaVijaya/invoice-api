package com.galvanize.orion.invoicify.service;

import com.galvanize.orion.invoicify.TestHelper.CompanyTestHelper;
import com.galvanize.orion.invoicify.dto.SimpleCompany;
import com.galvanize.orion.invoicify.entities.Company;
import com.galvanize.orion.invoicify.repository.CompanyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class CompanyServiceTest {

    @MockBean
    private CompanyRepository companyRepository;

    @InjectMocks
    CompanyService companyService;

    @Test
    public void getAllCompaniesTest_withZeroCompanies() {
        when(companyRepository.findAllByArchived(false)).thenReturn(new ArrayList<>());
        List<Company> result = companyService.getAllCompanies();
        assertEquals(0, result.size());
        verify(companyRepository, times(1)).findAllByArchived(false);
    }

    @Test
    public void getAllCompaniesTest_withOneCompany() {
        List<Company> companies = new ArrayList<>();
        companies.add(CompanyTestHelper.getCompanyOne());
        when(companyRepository.findAllByArchived(false)).thenReturn(companies);
        List<Company> result = companyService.getAllCompanies();
        assertEquals(1, result.size());
        assertEquals(CompanyTestHelper.getCompanyOne(), result.get(0));
        verify(companyRepository, times(1)).findAllByArchived(false);
    }

    @Test
    public void getAllCompaniesTest_withMultipleCompanies() {
        List<Company> companies = new ArrayList<>();
        companies.add(CompanyTestHelper.getCompanyOne());
        companies.add(CompanyTestHelper.getCompanyTwo());
        when(companyRepository.findAllByArchived(false)).thenReturn(companies);
        List<Company> result = companyService.getAllCompanies();
        assertEquals(2, result.size());
        assertEquals(CompanyTestHelper.getCompanyOne(), result.get(0));
        assertEquals(CompanyTestHelper.getCompanyTwo(), result.get(1));
        verify(companyRepository, times(1)).findAllByArchived(false);
    }

    @Test
    public void getAllSimpleCompaniesTest() {
        List<Company> companies = new ArrayList<>();
        companies.add(CompanyTestHelper.getCompanyOne());
        companies.add(CompanyTestHelper.getCompanyTwo());
        when(companyRepository.findAllByArchived(false)).thenReturn(companies);
        List<SimpleCompany> result = companyService.getAllSimpleCompanies();
        assertEquals(2, result.size());
        assertEquals(CompanyTestHelper.getCompanyOne().getName(), result.get(0).getName());
        assertEquals(CompanyTestHelper.getCompanyOne().getCity(), result.get(0).getCity());
        assertEquals(CompanyTestHelper.getCompanyOne().getState(), result.get(0).getState());
        assertEquals(CompanyTestHelper.getCompanyTwo().getName(), result.get(1).getName());
        assertEquals(CompanyTestHelper.getCompanyTwo().getCity(), result.get(1).getCity());
        assertEquals(CompanyTestHelper.getCompanyTwo().getState(), result.get(1).getState());
        verify(companyRepository, times(1)).findAllByArchived(false);

    }

    @Test
    public void test_addCompany() {

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
}
