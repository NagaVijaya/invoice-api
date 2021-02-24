package com.galvanize.orion.invoicify.controller;

import org.apache.commons.lang3.builder.ToStringExclude;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class InvoiceControllerUnitTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    public void test_getAllInvoicesEndPoint_ReturnsOk() throws Exception {
        mockMvc.perform(get("/api/v1/invoices"))
                .andExpect(status().isOk());
    }

    @Test
    public void  test_getAllInvoices_ReturnsObject() throws Exception {
        mockMvc.perform(get("/api/v1/invoices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());
    }


}
