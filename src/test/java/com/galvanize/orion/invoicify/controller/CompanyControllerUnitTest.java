package com.galvanize.orion.invoicify.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.orion.invoicify.TestHelper.CompanyTestHelper;
import com.galvanize.orion.invoicify.dto.SimpleCompany;
import com.galvanize.orion.invoicify.entities.Company;
import com.galvanize.orion.invoicify.service.CompanyService;
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

}
