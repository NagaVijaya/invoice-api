package com.galvanize.orion.invoicify.service;

import com.galvanize.orion.invoicify.TestHelper.CompanyTestHelper;
import com.galvanize.orion.invoicify.TestHelper.InvoiceTestHelper;
import com.galvanize.orion.invoicify.dto.SimpleCompany;
import com.galvanize.orion.invoicify.entities.Company;
import com.galvanize.orion.invoicify.entities.Invoice;
import com.galvanize.orion.invoicify.exception.CompanyDoesNotExist;
import com.galvanize.orion.invoicify.exception.DuplicateCompanyException;
import com.galvanize.orion.invoicify.exception.UnpaidInvoiceExistException;
import com.galvanize.orion.invoicify.repository.CompanyRepository;
import com.galvanize.orion.invoicify.repository.InvoiceRepository;
import com.galvanize.orion.invoicify.utilities.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;

import javax.transaction.Transactional;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@Transactional
public class CompanyServiceTest {

    @MockBean
    private CompanyRepository companyRepository;

    @MockBean
    private InvoiceRepository invoiceRepository;

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
    public void test_addCompany() throws DuplicateCompanyException {

        Company company = CompanyTestHelper.getCompany1();
        Company createdCompany = CompanyTestHelper.getCompany1();
        createdCompany.setId(UUID.fromString("4fa30ded-c47c-436a-9616-7e3b36be84b2"));
        when(companyRepository.saveAndFlush(any())).thenReturn(createdCompany);
        Company expectedCompany = companyService.addCompany(company);
        assertEquals(expectedCompany.getId(), createdCompany.getId());
        assertEquals(expectedCompany.getAddress(), createdCompany.getAddress());
        assertEquals(expectedCompany.getState(), createdCompany.getState());
        assertEquals(expectedCompany.getCity(), createdCompany.getCity());
        assertEquals(expectedCompany.getName(), createdCompany.getName());
        assertEquals(expectedCompany.getZipCode(), createdCompany.getZipCode());

        verify(companyRepository, times(1)).saveAndFlush(any());
    }

    @Test
    public void test_addDuplicateCompany() throws DuplicateCompanyException {

        Company company = CompanyTestHelper.getCompany1();
        when(companyRepository.saveAndFlush(any())).thenThrow(DataIntegrityViolationException.class);
        Exception exception = assertThrows(DuplicateCompanyException.class, () -> {
            companyService.addCompany(company);
        });
        String actualMessage = exception.getMessage();
        assertEquals(Constants.DUPLICATE_COMPANY_MESSAGE, actualMessage);
    }

    @Test
    public void test_modifyCompany() throws CompanyDoesNotExist, DuplicateCompanyException {
        Company existingCompany = CompanyTestHelper.getExistingCompany1();
        Company modifiedCompany = CompanyTestHelper.getExistingCompany1();
        modifiedCompany.setZipCode("18654");
        modifiedCompany.setCity("Austin");
        when(companyRepository.findById(any())).thenReturn(Optional.of(existingCompany));
        when(companyRepository.saveAndFlush(any())).thenReturn(modifiedCompany);
        Company expectedCompany = companyService.modifyCompany(modifiedCompany.getId().toString(), modifiedCompany);
        assertEquals(expectedCompany.getId(), modifiedCompany.getId());
        assertEquals(expectedCompany.getAddress(), modifiedCompany.getAddress());
        assertEquals(expectedCompany.getState(), modifiedCompany.getState());
        assertEquals(expectedCompany.getCity(), modifiedCompany.getCity());
        assertEquals(expectedCompany.getName(), modifiedCompany.getName());
        assertEquals(expectedCompany.getZipCode(), modifiedCompany.getZipCode());

        verify(companyRepository, times(1)).findById(any());
        verify(companyRepository, times(1)).saveAndFlush(any());
    }

    @Test
    public void test_modifyCompany_throws_DuplicateCompanyException() throws CompanyDoesNotExist, DuplicateCompanyException {
        Company existingCompany = CompanyTestHelper.getExistingCompany1();
        Company modifiedCompany = CompanyTestHelper.getExistingCompany1();
        modifiedCompany.setZipCode("18654");
        modifiedCompany.setCity("Austin");

        when(companyRepository.findById(any())).thenReturn(Optional.of(existingCompany));
        when(companyRepository.saveAndFlush(any())).thenThrow(DataIntegrityViolationException.class);
        Exception exception = assertThrows(DuplicateCompanyException.class, () -> {
            companyService.modifyCompany(modifiedCompany.getId().toString(),modifiedCompany);
        });
        String actualMessage = exception.getMessage();
        assertEquals(Constants.DUPLICATE_COMPANY_MESSAGE, actualMessage);

        verify(companyRepository, times(1)).findById(any());
        verify(companyRepository, times(1)).saveAndFlush(any());
    }

    @Test
    public void test_modifyNonExistentCompany_throws_CompanyDoesNotExist() {
        Company modifiedCompany = CompanyTestHelper.getExistingCompany1();
        modifiedCompany.setZipCode("18654");
        modifiedCompany.setCity("Austin");
        when(companyRepository.findById(any())).thenReturn(Optional.empty());

        CompanyDoesNotExist companyDoesNotExist = assertThrows(CompanyDoesNotExist.class, () -> companyService.modifyCompany(modifiedCompany.getId().toString(), modifiedCompany));
        assertEquals(Constants.COMPANY_DOES_NOT_EXIST, companyDoesNotExist.getMessage());

        verify(companyRepository, times(1)).findById(any());
    }

    @Test
    public void test_deleteCompany() throws CompanyDoesNotExist, UnpaidInvoiceExistException {
        Company existingCompany = CompanyTestHelper.getExistingCompany1();
        Company deleteCompany = CompanyTestHelper.getExistingCompany1();
        deleteCompany.setArchived(true);
        when(companyRepository.findById(any())).thenReturn(Optional.of(existingCompany));
        when(companyRepository.saveAndFlush(any())).thenReturn(deleteCompany);
        Company expectedCompany = companyService.deleteCompany(deleteCompany.getId().toString());
        assertEquals(expectedCompany.getId(), deleteCompany.getId());
        assertTrue(deleteCompany.isArchived());
        verify(companyRepository, times(1)).saveAndFlush(any());
    }

    @Test
    public void test_deleteNonExistentCompany_throws_CompanyDoesNotExist() {
        Company deleteCompany = CompanyTestHelper.getExistingCompany1();
        deleteCompany.setArchived(true);
        when(companyRepository.findById(any())).thenReturn(Optional.empty());

        CompanyDoesNotExist companyDoesNotExist = assertThrows(CompanyDoesNotExist.class, () -> companyService.deleteCompany(deleteCompany.getId().toString()));
        assertEquals(Constants.COMPANY_DOES_NOT_EXIST, companyDoesNotExist.getMessage());

        verify(companyRepository, times(1)).findById(any());
    }

    @Test
    public void test_deleteCompany_throws_UnpaidInvoiceExist() {
        Company deleteCompany = CompanyTestHelper.getCompanyWithInvoicesList();
        when(companyRepository.findById(any())).thenReturn(Optional.of(deleteCompany));
        UnpaidInvoiceExistException unpaidInvoiceExistException = assertThrows(UnpaidInvoiceExistException.class, () -> companyService.deleteCompany(deleteCompany.getId().toString()));
        assertEquals(Constants.UNPAID_INVOICE_EXIST_CAN_NOT_DELETE_COMPANY, unpaidInvoiceExistException.getMessage());
        verify(companyRepository, times(1)).findById(any());
    }

    @Test
    public void testGetInvoiceByCompany() throws CompanyDoesNotExist {

        Company company = CompanyTestHelper.getCompanyWithInvoices();
        when(invoiceRepository.findByCompany_Name(anyString())).thenReturn(company.getInvoices());
        when(companyRepository.findByName(anyString())).thenReturn(company);
        List<Invoice> invoiceList = companyService.getInvoicesByCompanyName(company.getName());
        assertEquals(invoiceList.size(), 2);
        assertEquals(invoiceList.get(0).getStatus(), company.getInvoices().get(0).getStatus());
        assertEquals(invoiceList.get(0).getAuthor(), company.getInvoices().get(0).getAuthor());
        assertEquals(invoiceList.get(1).getStatus(), company.getInvoices().get(1).getStatus());
        assertEquals(invoiceList.get(1).getAuthor(), company.getInvoices().get(1).getAuthor());

        verify(invoiceRepository, times(1)).findByCompany_Name(anyString());
    }

    @Test
    public void testGetInvoiceByCompany_throwsException(){

        Company company = CompanyTestHelper.getCompanyWithInvoices();
        when(companyRepository.findByName(anyString())).thenReturn(null);
        CompanyDoesNotExist companyDoesNotExist = assertThrows(CompanyDoesNotExist.class, () -> companyService.getInvoicesByCompanyName("Non Existing name"));
        assertEquals(Constants.COMPANY_DOES_NOT_EXIST, companyDoesNotExist.getMessage());

        verify(companyRepository, times(1)).findByName(anyString());

    }
}
