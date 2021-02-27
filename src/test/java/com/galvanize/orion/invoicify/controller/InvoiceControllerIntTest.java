package com.galvanize.orion.invoicify.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.orion.invoicify.entities.Invoice;
import com.galvanize.orion.invoicify.entities.LineItem;
import com.galvanize.orion.invoicify.repository.InvoiceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class InvoiceControllerIntTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private ObjectMapper mapper;

    @Test
    @DisplayName("Integration Test for creating new invoice with no line item")
    public void testCreateInvoiceWithNoLineItem() throws Exception{
        Invoice invoice = Invoice.builder().author("Gokul").company("Cognizant").lineItem(new ArrayList<>()).build();
        mvc.perform(post("/api/v1/invoice").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(invoice)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.author").value(invoice.getAuthor()))
                .andExpect(jsonPath("$.company").value(invoice.getCompany()))
                .andExpect(jsonPath("$.totalCost").value(0))
                .andExpect(jsonPath("$.createdDate").exists())
                .andExpect(jsonPath("$.lineItem").isEmpty());
    }

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

    @Test
    @DisplayName("Integration Test for creating new invoice with multiple line item")
    public void testCreateInvoiceWithMultipleLineItem() throws Exception{
        LineItem lineItem = LineItem.builder().description("project 1").quantity(10).rate(5.4).build();
        LineItem lineItem2 = LineItem.builder().description("project 2").quantity(10).rate(4.6).build();
        List<LineItem> lineItemList = new ArrayList<>();
        lineItemList.add(lineItem);
        lineItemList.add(lineItem2);
        Invoice invoice = Invoice.builder().lineItem(lineItemList).author("Gokul").company("Cognizant").build();
        mvc.perform(post("/api/v1/invoice").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(invoice)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.author").value(invoice.getAuthor()))
                .andExpect(jsonPath("$.company").value(invoice.getCompany()))
                .andExpect(jsonPath("$.totalCost").value(100))
                .andExpect(jsonPath("$.createdDate").exists())
                .andExpect(jsonPath("$.lineItem", hasSize(2)))
                .andExpect(jsonPath("$.lineItem[0].id").exists())
                .andExpect(jsonPath("$.lineItem[0].description").value(lineItem.getDescription()))
                .andExpect(jsonPath("$.lineItem[0].quantity").value(lineItem.getQuantity()))
                .andExpect(jsonPath("$.lineItem[0].rate").value(lineItem.getRate()))
                .andExpect(jsonPath("$.lineItem[0].fee").value(54))
                .andExpect(jsonPath("$.lineItem[1].id").exists())
                .andExpect(jsonPath("$.lineItem[1].description").value(lineItem2.getDescription()))
                .andExpect(jsonPath("$.lineItem[1].quantity").value(lineItem2.getQuantity()))
                .andExpect(jsonPath("$.lineItem[1].rate").value(lineItem2.getRate()))
                .andExpect(jsonPath("$.lineItem[1].fee").value(46));
    }

    @Test
    @DisplayName("Integration Test for adding single lineItem to an existing invoice")
    public void testAddSingleLineItemToExistingInvoice() throws Exception {
        LineItem lineItem = LineItem.builder().description("project 1").quantity(10).rate(5.4).build();
        List<LineItem> lineItemList = new ArrayList<>();
        lineItemList.add(lineItem);
        Invoice invoice = Invoice.builder().lineItem(lineItemList).author("Gokul").company("Cognizant").build();
        MvcResult result = mvc.perform(post("/api/v1/invoice").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(invoice)))
               .andReturn();

        Invoice existingInvoice = mapper.readValue(result.getResponse().getContentAsString(),Invoice.class);
        LineItem lineItem2 = LineItem.builder().description("project 2").quantity(10).rate(4.6).build();

        mvc.perform(put("/api/v1/invoice/"+existingInvoice.getId()).contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(lineItem2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingInvoice.getId().toString()))
                .andExpect(jsonPath("$.author").value(existingInvoice.getAuthor()))
                .andExpect(jsonPath("$.company").value(existingInvoice.getCompany()))
                .andExpect(jsonPath("$.totalCost").value(100))
                .andExpect(jsonPath("$.createdDate").exists())
                .andExpect(jsonPath("$.lineItem", hasSize(2)))
                .andExpect(jsonPath("$.lineItem[0].id").value(existingInvoice.getLineItem().get(0).getId().toString()))
                .andExpect(jsonPath("$.lineItem[0].description").value(lineItem.getDescription()))
                .andExpect(jsonPath("$.lineItem[0].quantity").value(lineItem.getQuantity()))
                .andExpect(jsonPath("$.lineItem[0].rate").value(lineItem.getRate()))
                .andExpect(jsonPath("$.lineItem[0].fee").value(54))
                .andExpect(jsonPath("$.lineItem[1].id").exists())
                .andExpect(jsonPath("$.lineItem[1].description").value(lineItem2.getDescription()))
                .andExpect(jsonPath("$.lineItem[1].quantity").value(lineItem2.getQuantity()))
                .andExpect(jsonPath("$.lineItem[1].rate").value(lineItem2.getRate()))
                .andExpect(jsonPath("$.lineItem[1].fee").value(46));

    }

    @Test
    public void test_getAllInvoices_returns_multipleInvoice() throws Exception {

        List<LineItem> lineItemList = new ArrayList<>();
        lineItemList.add(LineItem.builder()
                .description("lineitem1")
                .build());
        Invoice invoice01 = Invoice.builder()
                .author("Peter")
                .lineItem(lineItemList)
                .createdDate(new Date(System.currentTimeMillis()-24*60*60*1000))
                .build();
        Invoice invoice02 = Invoice.builder()
                .author("Naga")
                .createdDate(new Date(System.currentTimeMillis()))
                .build();
        invoiceRepository.save(invoice01);
        invoiceRepository.save(invoice02);

        mvc.perform(get("/api/v1/invoices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].author").value("Peter"))
                .andExpect(jsonPath("$[0].lineItem[0].description").value("lineitem1"))
                .andExpect(jsonPath("$[1].author").value("Naga"));
    }

    @Test
    public void test_addLineItem_exceptionThrownWhenInvoiceDoesNotExist() throws Exception {
        LineItem lineItem2 = LineItem.builder().description("project 2").quantity(10).rate(4.6).build();

        mvc.perform(put("/api/v1/invoice/4fa30ded-c47c-436a-9616-7e3b36be84b2").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(lineItem2)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Invoice does not exist"));

    }

}
