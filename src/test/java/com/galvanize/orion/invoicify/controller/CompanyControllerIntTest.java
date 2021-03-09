package com.galvanize.orion.invoicify.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.orion.invoicify.TestHelper.CompanyTestHelper;
import com.galvanize.orion.invoicify.TestHelper.InvoiceTestHelper;
import com.galvanize.orion.invoicify.entities.Company;
import com.galvanize.orion.invoicify.entities.Invoice;
import com.galvanize.orion.invoicify.exception.DuplicateCompanyException;
import com.galvanize.orion.invoicify.repository.CompanyRepository;
import com.galvanize.orion.invoicify.utilities.Constants;
import com.galvanize.orion.invoicify.repository.CompanyRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Propagation;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @Test
    public void test_getAllCompanies_returnsMultipleCompanies() throws Exception {
        List<Company> companyList = new ArrayList<>();
        companyList.add(CompanyTestHelper.getCompanyOne());
        companyList.add(CompanyTestHelper.getCompanyTwo());
        companyList.add(CompanyTestHelper.getArchivedCompany());
        companyRepository.saveAll(companyList);


        mockMvc.perform(get("/api/v1/companies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Company One"))
                .andExpect(jsonPath("$[1].name").value("Company Two"));
    }

    @Test
    public void test_getAllSimpleCompanies_returnsMultipleCompanies() throws Exception {
        List<Company> companyList = new ArrayList<>();
        companyList.add(CompanyTestHelper.getCompanyOne());
        companyList.add(CompanyTestHelper.getCompanyTwo());
        companyList.add(CompanyTestHelper.getArchivedCompany());
        companyRepository.saveAll(companyList);


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
                .andExpect(jsonPath("$[1].zipCode").doesNotExist())
                .andExpect(jsonPath("$[1].city").value("Columbus"))
                .andExpect(jsonPath("$[1].state").value("OH"));
    }

    @Test
    public void test_getInvoicesByCompany_returnsListOfInvoices() throws Exception {
        Company company = CompanyTestHelper.getCompany1();
        Company companyFromDatabase = companyRepository.saveAndFlush(company);
        Invoice invoice1 = InvoiceTestHelper.getUnpaidInvoiceListWithNoCompany1();
        invoice1.setCompany(companyFromDatabase);
        Invoice invoice2 = InvoiceTestHelper.getUnpaidInvoiceListWithNoCompany2();
        invoice2.setCompany(companyFromDatabase);

        mockMvc.perform(post("/api/v1/invoice").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(invoice1)))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/v1/invoice").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(invoice2)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/companies/invoices/" + companyFromDatabase.getName()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }


}
