package com.galvanize.orion.invoicify.controller;

import com.galvanize.orion.invoicify.entities.Company;
import com.galvanize.orion.invoicify.service.CompanyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = CompanyController.class)
public class CompanyControllerUnitTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    CompanyService companyService;

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
    public void test_getAllCompanies_returnsSingleCompany() throws Exception {
        List<Company> companyList = new ArrayList<>();
        Company companyOne = Company.builder()
                                    .name("Company One")
                                    .build();
        companyList.add(companyOne);
        when(companyService.getAllCompanies()).thenReturn(companyList);

        mockMvc.perform(get("/api/v1/companies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Company One"));
    }
}
