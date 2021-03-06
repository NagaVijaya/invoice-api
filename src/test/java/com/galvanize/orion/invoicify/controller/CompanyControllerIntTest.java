package com.galvanize.orion.invoicify.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.orion.invoicify.TestHelper.CompanyTestHelper;
import com.galvanize.orion.invoicify.entities.Company;
import com.galvanize.orion.invoicify.exception.DuplicateCompanyException;
import com.galvanize.orion.invoicify.repository.CompanyRepository;
import com.galvanize.orion.invoicify.utilities.Constants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Propagation;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CompanyControllerIntTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    public void test_addCompany() throws Exception {
        Company company = CompanyTestHelper.getCompany1();
        mockMvc.perform(post("/api/v1/company")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(company)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(company.getName()))
                .andExpect(jsonPath("$.address").value(company.getAddress()))
                .andExpect(jsonPath("$.state").value(company.getState()))
                .andExpect(jsonPath("$.city").value(company.getCity()))
                .andExpect(jsonPath("$.zipCode").value(company.getZipCode()));
    }

    @Test
    public void test_addDuplicateCompany() throws Exception {
        Company company = CompanyTestHelper.getCompany1();
        Company duplicateCompany = CompanyTestHelper.getCompany1();
        companyRepository.saveAndFlush(company);
        mockMvc.perform(post("/api/v1/company")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateCompany)))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.message").value(Constants.DUPLICATE_COMPANY_MESSAGE));
    }
}
