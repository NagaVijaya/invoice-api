package com.galvanize.orion.invoicify.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class InvoiceControllerIntTest {

    @Autowired
    private MockMvc mvc;


    @Test
    @DisplayName("Integration Test for creating new invoice")
    public void testCreateInvoice() throws Exception{
        mvc.perform(post("/api/v1/invoice").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }
}
