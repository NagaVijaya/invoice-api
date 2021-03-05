package com.galvanize.orion.invoicify.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = CompanyController.class)
public class CompanyControllerUnitTest {

    @Autowired
    MockMvc mockMvc;

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
}
