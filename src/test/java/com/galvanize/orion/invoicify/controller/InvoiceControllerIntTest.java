package com.galvanize.orion.invoicify.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.orion.invoicify.InvoiceHelper.InvoiceTestHelper;
import com.galvanize.orion.invoicify.entities.Invoice;
import com.galvanize.orion.invoicify.entities.LineItem;
import com.galvanize.orion.invoicify.repository.InvoiceRepository;
import com.galvanize.orion.invoicify.repository.LineItemRepository;
import com.galvanize.orion.invoicify.testUtilities.InvoiceData;
import com.galvanize.orion.invoicify.utilities.StatusEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;

import java.util.Collections;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    private LineItemRepository lineItemRepository;

    @Autowired
    private ObjectMapper mapper;

    @Test
    @DisplayName("Integration Test for creating new invoice with no line item")
    public void testCreateInvoiceWithNoLineItem() throws Exception {
        Invoice invoice = Invoice.builder().author("Gokul").company("Cognizant").lineItems(new ArrayList<>()).build();
        mvc.perform(post("/api/v1/invoice").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(invoice)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.author").value(invoice.getAuthor()))
                .andExpect(jsonPath("$.company").value(invoice.getCompany()))
                .andExpect(jsonPath("$.totalCost").value(0))
                .andExpect(jsonPath("$.createdDate").exists())
                .andExpect(jsonPath("$.lineItems").isEmpty());
    }

    @Test
    @DisplayName("Integration Test for creating new invoice with one line item")
    public void testCreateInvoiceWithOneLineItem() throws Exception {
        LineItem lineItem = LineItem.builder()
                                    .description("project 1")
                                    .quantity(10)
                                    .rate(BigDecimal.valueOf(5.4))
                                    .build();
        List<LineItem> lineItemList = new ArrayList<>();
        lineItemList.add(lineItem);
        Invoice invoice = Invoice.builder()
                                    .lineItems(lineItemList)
                                    .author("Gokul")
                                    .company("Cognizant")
                                    .build();
        mvc.perform(post("/api/v1/invoice").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(invoice)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.author").value(invoice.getAuthor()))
                .andExpect(jsonPath("$.company").value(invoice.getCompany()))
                .andExpect(jsonPath("$.totalCost").value(54))
                .andExpect(jsonPath("$.createdDate").exists())
                .andExpect(jsonPath("$.lineItems[0].id").exists())
                .andExpect(jsonPath("$.lineItems[0].description").value(lineItem.getDescription()))
                .andExpect(jsonPath("$.lineItems[0].quantity").value(lineItem.getQuantity()))
                .andExpect(jsonPath("$.lineItems[0].rate").value(lineItem.getRate()))
                .andExpect(jsonPath("$.lineItems[0].fee").value(54));
    }

    @Test
    @DisplayName("Integration Test for creating new invoice with multiple line item")
    public void testCreateInvoiceWithMultipleLineItem() throws Exception {
        LineItem lineItem = LineItem.builder()
                            .description("project 1")
                            .quantity(10)
                            .rate(BigDecimal.valueOf(5.4))
                            .build();
        LineItem lineItem2 = LineItem.builder()
                                        .description("project 2")
                                        .quantity(10)
                                        .rate(BigDecimal.valueOf(4.6))
                                        .build();
        List<LineItem> lineItemList = new ArrayList<>();
        lineItemList.add(lineItem);
        lineItemList.add(lineItem2);
        Invoice invoice = Invoice.builder().lineItems(lineItemList).author("Gokul").company("Cognizant").build();
        mvc.perform(post("/api/v1/invoice").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(invoice)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.author").value(invoice.getAuthor()))
                .andExpect(jsonPath("$.company").value(invoice.getCompany()))
                .andExpect(jsonPath("$.totalCost").value(100))
                .andExpect(jsonPath("$.createdDate").exists())
                .andExpect(jsonPath("$.lineItems", hasSize(2)))
                .andExpect(jsonPath("$.lineItems[0].id").exists())
                .andExpect(jsonPath("$.lineItems[0].description").value(lineItem.getDescription()))
                .andExpect(jsonPath("$.lineItems[0].quantity").value(lineItem.getQuantity()))
                .andExpect(jsonPath("$.lineItems[0].rate").value(lineItem.getRate()))
                .andExpect(jsonPath("$.lineItems[0].fee").value(54))
                .andExpect(jsonPath("$.lineItems[1].id").exists())
                .andExpect(jsonPath("$.lineItems[1].description").value(lineItem2.getDescription()))
                .andExpect(jsonPath("$.lineItems[1].quantity").value(lineItem2.getQuantity()))
                .andExpect(jsonPath("$.lineItems[1].rate").value(lineItem2.getRate()))
                .andExpect(jsonPath("$.lineItems[1].fee").value(46));
    }

    @Test
    @DisplayName("Integration Test for adding single lineItem to an existing invoice")
    public void testAddSingleLineItemToExistingInvoice() throws Exception {
        LineItem lineItem = LineItem
                            .builder()
                            .description("project 1")
                            .quantity(10)
                            .rate(BigDecimal.valueOf(5.4))
                            .build();
        List<LineItem> lineItemList = new ArrayList<>();
        lineItemList.add(lineItem);
        Invoice invoice = Invoice.builder().lineItems(lineItemList).author("Gokul").company("Cognizant").build();
        MvcResult result = mvc.perform(post("/api/v1/invoice").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(invoice)))
                .andReturn();

        Invoice existingInvoice = mapper.readValue(result.getResponse().getContentAsString(), Invoice.class);
        LineItem lineItem2 = LineItem.builder()
                                        .description("project 2")
                                        .quantity(10)
                                        .rate(BigDecimal.valueOf(4.6))
                                        .build();

        mvc.perform(put("/api/v1/invoice/" + existingInvoice.getId()).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Arrays.asList(lineItem2))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingInvoice.getId().toString()))
                .andExpect(jsonPath("$.author").value(existingInvoice.getAuthor()))
                .andExpect(jsonPath("$.company").value(existingInvoice.getCompany()))
                .andExpect(jsonPath("$.totalCost").value(100))
                .andExpect(jsonPath("$.createdDate").exists())
                .andExpect(jsonPath("$.lineItems", hasSize(2)))
                .andExpect(jsonPath("$.lineItems[0].id").value(existingInvoice.getLineItems().get(0).getId().toString()))
                .andExpect(jsonPath("$.lineItems[0].description").value(lineItem.getDescription()))
                .andExpect(jsonPath("$.lineItems[0].quantity").value(lineItem.getQuantity()))
                .andExpect(jsonPath("$.lineItems[0].rate").value(lineItem.getRate()))
                .andExpect(jsonPath("$.lineItems[0].fee").value(54))
                .andExpect(jsonPath("$.lineItems[1].id").exists())
                .andExpect(jsonPath("$.lineItems[1].description").value(lineItem2.getDescription()))
                .andExpect(jsonPath("$.lineItems[1].quantity").value(lineItem2.getQuantity()))
                .andExpect(jsonPath("$.lineItems[1].rate").value(lineItem2.getRate()))
                .andExpect(jsonPath("$.lineItems[1].fee").value(46));

    }

    @Test
    @DisplayName("Integration Test for adding multiple lineItems to an existing invoice")
    public void testAddMultipleLineItemsToExistingInvoice() throws Exception {
        LineItem lineItem = InvoiceTestHelper.getLineItem();
        List<LineItem> lineItemList = new ArrayList<>();
        lineItemList.add(lineItem);
        Invoice invoice = Invoice.builder().lineItems(lineItemList).author("Gokul").company("Cognizant").build();
        MvcResult result = mvc.perform(post("/api/v1/invoice").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(invoice)))
                .andReturn();

        Invoice existingInvoice = mapper.readValue(result.getResponse().getContentAsString(), Invoice.class);
        LineItem lineItem2 = InvoiceTestHelper.getLineItem2();
        LineItem lineItem3 = InvoiceTestHelper.getLineItem3();


        mvc.perform(put("/api/v1/invoice/" + existingInvoice.getId()).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Arrays.asList(lineItem2, lineItem3))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingInvoice.getId().toString()))
                .andExpect(jsonPath("$.author").value(existingInvoice.getAuthor()))
                .andExpect(jsonPath("$.company").value(existingInvoice.getCompany()))
                .andExpect(jsonPath("$.totalCost").value(150))
                .andExpect(jsonPath("$.createdDate").exists())
                .andExpect(jsonPath("$.lineItems", hasSize(3)))
                .andExpect(jsonPath("$.lineItems[0].id").value(existingInvoice.getLineItems().get(0).getId().toString()))
                .andExpect(jsonPath("$.lineItems[0].description").value(lineItem.getDescription()))
                .andExpect(jsonPath("$.lineItems[0].quantity").value(lineItem.getQuantity()))
                .andExpect(jsonPath("$.lineItems[0].rate").value(lineItem.getRate()))
                .andExpect(jsonPath("$.lineItems[0].fee").value(54))
                .andExpect(jsonPath("$.lineItems[1].id").exists())
                .andExpect(jsonPath("$.lineItems[1].description").value(lineItem2.getDescription()))
                .andExpect(jsonPath("$.lineItems[1].quantity").value(lineItem2.getQuantity()))
                .andExpect(jsonPath("$.lineItems[1].rate").value(lineItem2.getRate()))
                .andExpect(jsonPath("$.lineItems[1].fee").value(46))
                .andExpect(jsonPath("$.lineItems[2].id").exists())
                .andExpect(jsonPath("$.lineItems[2].description").value(lineItem3.getDescription()))
                .andExpect(jsonPath("$.lineItems[2].quantity").value(lineItem3.getQuantity()))
                .andExpect(jsonPath("$.lineItems[2].rate").value(lineItem3.getRate()))
                .andExpect(jsonPath("$.lineItems[2].fee").value(50));

    }

    @Test
    @DisplayName("Integration test for GET invoices when database is empty")
    public void test_getAllInvoicesWhenEmpty_returnsEmptyList() throws Exception {

        mvc.perform(get("/api/v1/invoices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("Integration test for GET invoices with paging and sorting by created date in ascending order")
    public void test_getAllInvoicesByPageNumber_returnsInvoicesByPageNumberAndSortedByCreateDate() throws Exception {


        List<Invoice> invoiceList = InvoiceData.GenerateInvoices();
        invoiceList.forEach(invoice -> invoiceRepository.save(invoice));

        mvc.perform(get("/api/v1/invoices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.length()").value(10))
                .andExpect(jsonPath("$[0].author").value("Author20"))
                .andExpect(jsonPath("$[9].author").value("Author11"));

        mvc.perform(get("/api/v1/invoices?page=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.length()").value(10))
                .andExpect(jsonPath("$[0].author").value("Author10"))
                .andExpect(jsonPath("$[9].author").value("Author01"));

        mvc.perform(get("/api/v1/invoices?page=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].author").value("Author00"));
    }

    @Test
    @DisplayName("Integration test throws exception when trying to add line item to non existent invoice ")
    public void test_addLineItem_exceptionThrownWhenInvoiceDoesNotExist() throws Exception {
        LineItem lineItem2 = LineItem.builder()
                                    .description("project 2")
                                    .quantity(10)
                                    .rate(BigDecimal.valueOf(4.6))
                                    .build();

        mvc.perform(put("/api/v1/invoice/4fa30ded-c47c-436a-9616-7e3b36be84b2").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Arrays.asList(lineItem2))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Invoice does not exist"));

    }

    @Test
    @DisplayName("Integration test throws exception when trying to add line item to non existent invoice ")
    public void test_addLineItem_exceptionThrownWhenInvoiceIsPaid() throws Exception {
        Invoice invoice = Invoice.builder().author("Gokul").lineItems(new ArrayList<>()).status(StatusEnum.PAID).company("Cognizant").build();
        MvcResult result = mvc.perform(post("/api/v1/invoice").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(invoice)))
                .andReturn();

        Invoice existingInvoice = mapper.readValue(result.getResponse().getContentAsString(), Invoice.class);

        LineItem lineItem2 = LineItem.builder()
                                    .description("project 2")
                                    .quantity(10)
                                    .rate(BigDecimal.valueOf(4.6))
                                    .build();

        mvc.perform(put("/api/v1/invoice/" + existingInvoice.getId()).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Collections.singletonList(lineItem2))))
                .andExpect(status().isNotModified())
                .andExpect(jsonPath("$.message").value("Invoice paid, cannot be modified"));

    }

    @Test
    @DisplayName("Integration test throws exception when trying to modify paid invoice ")
    public void test_modifyPaidInvoice_throwsInvoiceModifyException() throws Exception {

        Invoice invoice = Invoice.builder().author("Gokul").lineItems(new ArrayList<>()).status(StatusEnum.PAID).company("Cognizant").build();
        MvcResult result = mvc.perform(post("/api/v1/invoice").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(invoice)))
                .andReturn();

        Invoice existingInvoice = mapper.readValue(result.getResponse().getContentAsString(), Invoice.class);

        mvc.perform(patch("/api/v1/invoice").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(existingInvoice)))
                .andExpect(status().isNotModified())
                .andExpect(jsonPath("$.message").value("Invoice paid, cannot be modified"));

    }

    @Test
    @DisplayName("Integration test to modify unpaid invoice")
    public void test_modifyUnPaidInvoice_withPaidStatus() throws Exception {

        Invoice invoice = Invoice.builder().author("Gokul").lineItems(new ArrayList<>()).status(StatusEnum.UNPAID).company("Cognizant").build();
        MvcResult result = mvc.perform(post("/api/v1/invoice").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(invoice)))
                .andReturn();

        Invoice existingInvoice = mapper.readValue(result.getResponse().getContentAsString(), Invoice.class);
        existingInvoice.setStatus(StatusEnum.PAID);
        mvc.perform(patch("/api/v1/invoice").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(existingInvoice)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(StatusEnum.PAID.toString()))
                .andExpect(jsonPath("$.modifiedDate").exists());
    }

    @Test
    @DisplayName("Integration test to delete invoice with line items")
    public void test_deleteInvoice_withLineItems() throws Exception {
        LocalDate localDate = LocalDate.now().minusYears(2);
        Invoice invoice = InvoiceTestHelper.getInvoiceWithTwoLineItem();
        invoice.setCreatedDate(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        Invoice savedInvoice = invoiceRepository.save(invoice);

        Optional<LineItem> lineItem1PriorDelete = lineItemRepository.findById(savedInvoice.getLineItems().get(0).getId());
        Optional<LineItem> lineItem2PriorDelete = lineItemRepository.findById(savedInvoice.getLineItems().get(1).getId());

        mvc.perform(delete("/api/v1/invoice/" + savedInvoice.getId().toString()))
                .andExpect(status().isOk());


        Optional<Invoice> removedInvoice = invoiceRepository.findById(savedInvoice.getId());
        Optional<LineItem> lineItem1PostDelete = lineItemRepository.findById(savedInvoice.getLineItems().get(0).getId());
        Optional<LineItem> lineItem2PostDelete = lineItemRepository.findById(savedInvoice.getLineItems().get(1).getId());

        assertFalse(removedInvoice.isPresent());
        assertTrue(lineItem1PriorDelete.isPresent());
        assertTrue(lineItem2PriorDelete.isPresent());
        assertFalse(lineItem1PostDelete.isPresent());
        assertFalse(lineItem2PostDelete.isPresent());

    }

    @Test
    @DisplayName("Integration test to delete invoice that does not exit")
    public void test_deleteInvoice_thatDoesNotExist() throws Exception {

        mvc.perform(delete("/api/v1/invoice/4fa30ded-c47c-436a-9616-7e3b36be84b2"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Invoice does not exist"));
    }

    @Test
    @DisplayName("Integration test to delete invoice that less than a year old")
    public void test_deleteInvoice_thatIsLessThanAYearOld() throws Exception {
        LocalDate localDate = LocalDate.now().minusDays(10);
        Invoice invoice = InvoiceTestHelper.getInvoiceWithTwoLineItem();
        invoice.setCreatedDate(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        Invoice savedInvoice = invoiceRepository.save(invoice);

        mvc.perform(delete("/api/v1/invoice/" + savedInvoice.getId().toString()))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.message").value("Invoice is less than 1 year old, can't delete!"));
    }
}
