package com.galvanize.orion.invoicify.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.orion.invoicify.TestHelper.CompanyTestHelper;
import com.galvanize.orion.invoicify.entities.Company;
import com.galvanize.orion.invoicify.exception.DuplicateCompanyException;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        mockMvc.perform(get("/api/v1/companies"))
                .andExpect(status().isOk());
    }

    @Test
    public void test_getAllInvoices_returnsObject() throws Exception {
        mockMvc.perform(get("/api/v1/companies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());
    }

    @Test
    public void test_getAllInvoices_returns_emptyList() throws Exception {
        List<String> companyList = new ArrayList<>();
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
}
