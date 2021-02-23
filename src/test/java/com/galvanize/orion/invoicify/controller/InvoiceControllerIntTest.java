package com.galvanize.orion.invoicify.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.orion.invoicify.entities.Invoice;
import com.galvanize.orion.invoicify.entities.LineItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class InvoiceControllerIntTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    @DisplayName("Integration Test for creating new invoice with one line item")
    public void testCreateInvoiceWithOneLineItem() throws Exception{
        LineItem lineItem = LineItem.builder().description("project 1").quantity(10).rate(5.4).build();
        List<LineItem> lineItemList = new ArrayList<>();
        lineItemList.add(lineItem);
        Invoice invoice = Invoice.builder().lineItem(lineItemList).author("Gokul").company("Cognizant").build();
        mvc.perform(post("/api/v1/invoice").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(invoice)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.author").value(invoice.getAuthor()))
                .andExpect(jsonPath("$.company").value(invoice.getCompany()))
                .andExpect(jsonPath("$.totalCost").value(54))
                .andExpect(jsonPath("$.createdDate").exists())
                .andExpect(jsonPath("$.lineItem[0].id").exists())
                .andExpect(jsonPath("$.lineItem[0].description").value(lineItem.getDescription()))
                .andExpect(jsonPath("$.lineItem[0].quantity").value(lineItem.getQuantity()))
                .andExpect(jsonPath("$.lineItem[0].rate").value(lineItem.getRate()))
                .andExpect(jsonPath("$.lineItem[0].fee").value(54));
    }
}
