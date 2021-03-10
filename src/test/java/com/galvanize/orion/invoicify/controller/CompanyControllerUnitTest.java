package com.galvanize.orion.invoicify.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.orion.invoicify.TestHelper.CompanyTestHelper;
import com.galvanize.orion.invoicify.dto.SimpleCompany;
import com.galvanize.orion.invoicify.entities.Company;
import com.galvanize.orion.invoicify.exception.CompanyDoesNotExist;
import com.galvanize.orion.invoicify.exception.DuplicateCompanyException;
import com.galvanize.orion.invoicify.exception.UnpaidInvoiceExistException;
import com.galvanize.orion.invoicify.service.CompanyService;
import com.galvanize.orion.invoicify.utilities.Constants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = CompanyController.class)
public class CompanyControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CompanyService companyService;

    @Test
    public void test_getAllCompaniesEndPoint_returnsOk() throws Exception {
        when(companyService.getAllCompanies()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/v1/companies"))
                .andExpect(status().isOk());
    }

    @Test
    public void test_getAllCompanies_returnsObject() throws Exception {
        when(companyService.getAllCompanies()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/v1/companies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());
    }

    @Test
    public void test_getAllCompanies_returns_emptyList() throws Exception {
        when(companyService.getAllCompanies()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/v1/companies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    public void test_addCompany() throws Exception {

        Company company = CompanyTestHelper.getCompany1();
        Company createdCompany = CompanyTestHelper.getCompany1();
        createdCompany.setId(UUID.fromString("4fa30ded-c47c-436a-9616-7e3b36be84b2"));
        when(companyService.addCompany(any())).thenReturn(createdCompany);
        mockMvc.perform(post("/api/v1/company")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(company)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(createdCompany.getName()))
                .andExpect(jsonPath("$.address").value(createdCompany.getAddress()))
                .andExpect(jsonPath("$.state").value(createdCompany.getState()))
                .andExpect(jsonPath("$.city").value(createdCompany.getCity()))
                .andExpect(jsonPath("$.zipCode").value(createdCompany.getZipCode()));
        verify(companyService, times(1)).addCompany(any());
    }

    @Test
    public void test_addDuplicateCompany() throws Exception {

        Company company = CompanyTestHelper.getCompany1();

        when(companyService.addCompany(any())).thenThrow(new DuplicateCompanyException());
        mockMvc.perform(post("/api/v1/company")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(company)))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.message").value(Constants.DUPLICATE_COMPANY_MESSAGE));
        verify(companyService, times(1)).addCompany(any());
    }

    @Test
    public void test_getAllCompanies_returnsSingleCompany() throws Exception {
        List<Company> companyList = new ArrayList<>();
        Company companyOne = CompanyTestHelper.getCompanyOne();
        companyList.add(companyOne);
        when(companyService.getAllCompanies()).thenReturn(companyList);

        mockMvc.perform(get("/api/v1/companies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Company One"));
    }

    @Test
    public void test_getAllCompanies_returnsMultipleCompanies() throws Exception {
        List<Company> companyList = new ArrayList<>();
        companyList.add(CompanyTestHelper.getCompanyOne());
        companyList.add(CompanyTestHelper.getCompanyTwo());
        when(companyService.getAllCompanies()).thenReturn(companyList);

        mockMvc.perform(get("/api/v1/companies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Company One"))
                .andExpect(jsonPath("$[0].address").exists())
                .andExpect(jsonPath("$[1].name").value("Company Two"))
                .andExpect(jsonPath("$[1].address").exists());
    }

    @Test
    public void test_getAllSimpleCompanies_returnsMultipleCompanies() throws Exception {
        List<SimpleCompany> companyList = new ArrayList<>();
        companyList.add(CompanyTestHelper.getSimpleCompanyOne());
        companyList.add(CompanyTestHelper.getSimpleCompanyTwo());
        when(companyService.getAllSimpleCompanies()).thenReturn(companyList);

        mockMvc.perform(get("/api/v1/companies/simple"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Company One"))
                .andExpect(jsonPath("$[0].city").value("Chicago"))
                .andExpect(jsonPath("$[0].state").value("IL"))
                .andExpect(jsonPath("$[0].address").doesNotExist())
                .andExpect(jsonPath("$[0].zipCode").doesNotExist())
                .andExpect(jsonPath("$[1].name").value("Company Two"))
                .andExpect(jsonPath("$[1].address").doesNotExist())
                .andExpect(jsonPath("$[1].city").value("Columbus"))
                .andExpect(jsonPath("$[1].state").value("OH"))
                .andExpect(jsonPath("$[1].zipCode").doesNotExist());
    }

    @Test
    public void test_getInvoiceByCompany() throws Exception {
        Company company = CompanyTestHelper.getCompanyWithInvoices();
        when(companyService.getInvoicesByCompanyName(anyString())).thenReturn(company.getInvoices());

        mockMvc.perform(get("/api/v1/companies/invoices/" + company.getName()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].author").value(company.getInvoices().get(0).getAuthor()))
                .andExpect(jsonPath("$[1].author").value(company.getInvoices().get(1).getAuthor()));
    }

    @Test
    public void test_modifyCompany() throws Exception {

        Company modifiedCompany = CompanyTestHelper.getExistingCompany1();
        modifiedCompany.setZipCode("18654");
        modifiedCompany.setCity("Austin");

        when(companyService.modifyCompany(any(), any())).thenReturn(modifiedCompany);
        mockMvc.perform(put("/api/v1/company/"+modifiedCompany.getId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(modifiedCompany)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(modifiedCompany.getId().toString()))
                .andExpect(jsonPath("$.name").value(modifiedCompany.getName()))
                .andExpect(jsonPath("$.address").value(modifiedCompany.getAddress()))
                .andExpect(jsonPath("$.state").value(modifiedCompany.getState()))
                .andExpect(jsonPath("$.city").value(modifiedCompany.getCity()))
                .andExpect(jsonPath("$.zipCode").value(modifiedCompany.getZipCode()));
        verify(companyService, times(1)).modifyCompany(any(), any());
    }

    @Test
    public void test_modifyCompany_throws_DuplicateCompanyException() throws Exception {

        Company modifiedCompany = CompanyTestHelper.getExistingCompany1();
        modifiedCompany.setId(UUID.randomUUID());
        modifiedCompany.setZipCode("18654");
        modifiedCompany.setCity("Austin");

        when(companyService.modifyCompany(any(), any())).thenThrow(new DuplicateCompanyException());
        mockMvc.perform(put("/api/v1/company/"+modifiedCompany.getId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(modifiedCompany)))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.message").value(Constants.DUPLICATE_COMPANY_MESSAGE));

        verify(companyService, times(1)).modifyCompany(any(), any());
    }

    @Test
    public void test_modifyNonExistentCompany_throws_CompanyDoesNotExist() throws Exception {

        Company modifiedCompany = CompanyTestHelper.getExistingCompany1();
        modifiedCompany.setId(UUID.randomUUID());
        modifiedCompany.setZipCode("18654");
        modifiedCompany.setCity("Austin");

        when(companyService.modifyCompany(any(), any())).thenThrow(new CompanyDoesNotExist());
        mockMvc.perform(put("/api/v1/company/"+modifiedCompany.getId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(modifiedCompany)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(Constants.COMPANY_DOES_NOT_EXIST));

        verify(companyService, times(1)).modifyCompany(any(), any());
    }

    @Test
    public void test_deleteCompany_paidAndNonArchivedInvoices() throws Exception {

        Company deleteCompany = CompanyTestHelper.getCompanyWithPaidArchivedInvoicesList();
        Company nonArchivedInvoiceCompany = CompanyTestHelper.getCompanyWithPaidNonArchivedInvoicesList();

        when(companyService.deleteCompany(deleteCompany.getId().toString())).thenReturn(deleteCompany);
        mockMvc.perform(delete("/api/v1/company/"+deleteCompany.getId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nonArchivedInvoiceCompany)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(deleteCompany.getId().toString()))
                .andExpect(jsonPath("$.name").value(deleteCompany.getName()))
                .andExpect(jsonPath("$.address").value(deleteCompany.getAddress()))
                .andExpect(jsonPath("$.state").value(deleteCompany.getState()))
                .andExpect(jsonPath("$.city").value(deleteCompany.getCity()))
                .andExpect(jsonPath("$.archived").value(true))
                .andExpect(jsonPath("$.invoices[0].archived").value(true))
                .andExpect(jsonPath("$.zipCode").value(deleteCompany.getZipCode()));

        verify(companyService, times(1)).deleteCompany(any());
    }

    @Test
    public void test_deleteNonExistentCompany_throws_CompanyDoesNotExist() throws Exception {

        Company deleteCompany = CompanyTestHelper.getExistingCompany1();

        when(companyService.deleteCompany(deleteCompany.getId().toString())).thenThrow(new CompanyDoesNotExist());

        mockMvc.perform(delete("/api/v1/company/"+deleteCompany.getId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deleteCompany)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(Constants.COMPANY_DOES_NOT_EXIST));
        verify(companyService, times(1)).deleteCompany(any());
    }

    @Test
    public void test_deleteCompany_throws_UnpaidInvoiceExist() throws Exception {

        Company deleteCompany = CompanyTestHelper.getCompanyWithInvoicesList();
        when(companyService.deleteCompany(deleteCompany.getId().toString())).thenThrow(new UnpaidInvoiceExistException());

        mockMvc.perform(delete("/api/v1/company/"+deleteCompany.getId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deleteCompany)))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.message").value(Constants.UNPAID_INVOICE_EXIST_CAN_NOT_DELETE_COMPANY));
        verify(companyService, times(1)).deleteCompany(any());
    }

}
