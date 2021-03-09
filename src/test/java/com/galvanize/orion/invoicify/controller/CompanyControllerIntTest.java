package com.galvanize.orion.invoicify.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.orion.invoicify.TestHelper.CompanyTestHelper;
import com.galvanize.orion.invoicify.TestHelper.InvoiceTestHelper;
import com.galvanize.orion.invoicify.entities.Company;
import com.galvanize.orion.invoicify.entities.Invoice;
import com.galvanize.orion.invoicify.exception.CompanyDoesNotExist;
import com.galvanize.orion.invoicify.exception.DuplicateCompanyException;
import com.galvanize.orion.invoicify.exception.UnpaidInvoiceExistException;
import com.galvanize.orion.invoicify.repository.CompanyRepository;
import com.galvanize.orion.invoicify.repository.InvoiceRepository;
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
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

    @Autowired
    private InvoiceRepository invoiceRepository;

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
    public void test_modifyCompany() throws Exception {

        Company company = CompanyTestHelper.getExistingCompany1();
        Company modifiedCompany = companyRepository.save(company);
        modifiedCompany.setZipCode("18654");
        modifiedCompany.setCity("Austin");

        mockMvc.perform(put("/api/v1/company/" + modifiedCompany.getId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(modifiedCompany)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(modifiedCompany.getId().toString()))
                .andExpect(jsonPath("$.name").value(modifiedCompany.getName()))
                .andExpect(jsonPath("$.address").value(modifiedCompany.getAddress()))
                .andExpect(jsonPath("$.state").value(modifiedCompany.getState()))
                .andExpect(jsonPath("$.city").value(modifiedCompany.getCity()))
                .andExpect(jsonPath("$.zipCode").value(modifiedCompany.getZipCode()));

    }

    @Test
    public void test_modifyCompany_throws_DuplicateCompanyException() throws Exception {

        Company existingCompany = CompanyTestHelper.getExistingCompany1();
        Company modifiedCompany = CompanyTestHelper.getExistingCompany1();
        modifiedCompany.setName("Pre-modified");
        existingCompany = companyRepository.saveAndFlush(existingCompany);
        modifiedCompany = companyRepository.saveAndFlush(modifiedCompany);

        modifiedCompany.setName(existingCompany.getName());
        modifiedCompany.setZipCode("18654");
        modifiedCompany.setCity("Austin");


        mockMvc.perform(put("/api/v1/company/" + modifiedCompany.getId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(modifiedCompany)))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.message").value(Constants.DUPLICATE_COMPANY_MESSAGE));


    }

    @Test
    public void test_modifyNonExistentCompany_throws_CompanyDoesNotExist() throws Exception {

        Company modifiedCompany = CompanyTestHelper.getExistingCompany1();
        modifiedCompany.setId(UUID.randomUUID());
        modifiedCompany.setZipCode("18654");
        modifiedCompany.setCity("Austin");


        mockMvc.perform(put("/api/v1/company/" + modifiedCompany.getId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(modifiedCompany)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(Constants.COMPANY_DOES_NOT_EXIST));


    }

    @Test
    public void test_deleteCompany() throws Exception {

        Company deleteCompany = CompanyTestHelper.getExistingCompany1();
        deleteCompany = companyRepository.saveAndFlush(deleteCompany);
        deleteCompany.setArchived(true);

        mockMvc.perform(delete("/api/v1/company/" + deleteCompany.getId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deleteCompany)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(deleteCompany.getId().toString()))
                .andExpect(jsonPath("$.name").value(deleteCompany.getName()))
                .andExpect(jsonPath("$.address").value(deleteCompany.getAddress()))
                .andExpect(jsonPath("$.state").value(deleteCompany.getState()))
                .andExpect(jsonPath("$.city").value(deleteCompany.getCity()))
                .andExpect(jsonPath("$.archived").value(true))
                .andExpect(jsonPath("$.zipCode").value(deleteCompany.getZipCode()));
    }

    @Test
    public void test_deleteNonExistentCompany_throws_CompanyDoesNotExist() throws Exception {
        Company deleteCompany = CompanyTestHelper.getExistingCompany1();
        deleteCompany.setArchived(true);

        mockMvc.perform(delete("/api/v1/company/" + deleteCompany.getId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deleteCompany)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(Constants.COMPANY_DOES_NOT_EXIST));
    }

    @Test
    public void test_deleteCompany_throws_UnpaidInvoiceExist() throws Exception {

        Company deleteCompany = CompanyTestHelper.getCompany1();
        deleteCompany = companyRepository.saveAndFlush(deleteCompany);
        Invoice unpaidInvoice = InvoiceTestHelper.getUnpaidInvoice();
        unpaidInvoice.setCompany(deleteCompany);
        unpaidInvoice = invoiceRepository.saveAndFlush(unpaidInvoice);
        deleteCompany.getInvoices().add(unpaidInvoice);
        deleteCompany = companyRepository.saveAndFlush(deleteCompany);

        mockMvc.perform(delete("/api/v1/company/" + deleteCompany.getId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deleteCompany)))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.message").value(Constants.UNPAID_INVOICE_EXIST_CAN_NOT_DELETE_COMPANY));
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
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].author").value(invoice1.getAuthor()))
                .andExpect(jsonPath("$[0].totalCost").value(100))
                .andExpect(jsonPath("$[1].author").value(invoice2.getAuthor()))
                .andExpect(jsonPath("$[1].totalCost").value(46));

    }


}
