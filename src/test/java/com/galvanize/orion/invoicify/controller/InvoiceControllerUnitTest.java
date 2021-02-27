package com.galvanize.orion.invoicify.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.orion.invoicify.InvoiceHelper.InvoiceTestHelper;
import com.galvanize.orion.invoicify.entities.Invoice;
import com.galvanize.orion.invoicify.entities.LineItem;
import com.galvanize.orion.invoicify.exception.InvoiceNotFoundException;
import com.galvanize.orion.invoicify.service.InvoiceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = InvoiceController.class)
public class InvoiceControllerUnitTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    InvoiceService invoiceService;

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void createInvoiceCallsInvoiceService() throws Exception {
        Invoice invoice = Invoice.builder().author("Gokul").company("Cognizant").lineItem(new ArrayList<>()).build();
        when(invoiceService.createInvoice(any())).thenReturn(invoice);
        mockMvc.perform(post("/api/v1/invoice").contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(invoice)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.author").value(invoice.getAuthor()))
                .andExpect(jsonPath("$.company").value(invoice.getCompany()));

        verify(invoiceService, times(1)).createInvoice(any());
    }

    @Test
    public void test_getAllInvoicesEndPoint_returnsOk() throws Exception {
        mockMvc.perform(get("/api/v1/invoices"))
                .andExpect(status().isOk());
    }

    @Test
    public void test_getAllInvoices_returnsObject() throws Exception {
        mockMvc.perform(get("/api/v1/invoices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());
    }

    @Test
    public void test_getAllInvoices_returns_emptyList() throws Exception {
        List<Invoice> invoiceList = new ArrayList<>();
        when(invoiceService.getAllInvoices(0)).thenReturn(invoiceList);
        mockMvc.perform(get("/api/v1/invoices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.length()").value(0));
        verify(invoiceService, times(1)).getAllInvoices(0);
    }

    @Test
    public void test_getAllInvoices_returns_singleInvoice() throws Exception {
        List<Invoice> invoiceList = new ArrayList<>();
        invoiceList.add(Invoice.builder()
                .author("Peter")
                .build());
        when(invoiceService.getAllInvoices(0)).thenReturn(invoiceList);

        mockMvc.perform(get("/api/v1/invoices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].author").value("Peter"));
        verify(invoiceService, times(1)).getAllInvoices(0);
    }

    @Test
    public void test_getAllInvoices_returns_multipleInvoice() throws Exception {
        List<Invoice> invoiceList = new ArrayList<>();
        invoiceList.add(Invoice.builder()
                .author("Peter")
                .build());
        invoiceList.add(Invoice.builder()
                .author("Naga")
                .build());
        when(invoiceService.getAllInvoices(0)).thenReturn(invoiceList);

        mockMvc.perform(get("/api/v1/invoices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].author").value("Peter"))
                .andExpect(jsonPath("$[1].author").value("Naga"));
        verify(invoiceService, times(1)).getAllInvoices(0);
    }

    @Test
    public void test_getAllInvoices_returns_InvoicesWithLineItem() throws Exception {

        List<Invoice> invoiceList = new ArrayList<>();
        List<LineItem> lineItemList = new ArrayList<>();
        lineItemList.add(LineItem.builder()
                .description("lineitem1")
                .build());
        invoiceList.add(Invoice.builder()
                .author("Peter")
                .lineItem(lineItemList)
                .build());
        when(invoiceService.getAllInvoices(0)).thenReturn(invoiceList);

        mockMvc.perform(get("/api/v1/invoices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].author").value("Peter"))
                .andExpect(jsonPath("$[0].lineItem[0].description").value("lineitem1"));
        verify(invoiceService, times(1)).getAllInvoices(0);

    }

    @Test
    public void test_getAllInvoices_handlesPageParameter() throws Exception {
        List<Invoice> invoiceList = new ArrayList<>();
        invoiceList.add(Invoice.builder()
                .author("Peter")
                .build());
        invoiceList.add(Invoice.builder()
                .author("Naga")
                .build());
        when(invoiceService.getAllInvoices(1)).thenReturn(invoiceList);

        mockMvc.perform(get("/api/v1/invoices?page=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].author").value("Peter"))
                .andExpect(jsonPath("$[1].author").value("Naga"));
        verify(invoiceService, times(1)).getAllInvoices(1);
    }



    @Test
    public void addLineItemToExistingInvoice() throws Exception {
        Invoice invoice = Invoice.builder().author("Gokul").company("Cognizant").lineItem(new ArrayList<>()).build();
        LineItem lineItem = LineItem.builder().description("project 1").quantity(10).rate(5.4).build();
        invoice.setLineItem(Collections.singletonList(lineItem));
        when(invoiceService.addLineItemToInvoice(any(UUID.class), any())).thenReturn(invoice);
        mockMvc.perform(put("/api/v1/invoice/4fa30ded-c47c-436a-9616-7e3b36be84b3").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(Collections.singletonList(lineItem))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.author").value(invoice.getAuthor()))
                .andExpect(jsonPath("$.company").value(invoice.getCompany()));

        verify(invoiceService, times(1)).addLineItemToInvoice(any(), any());
    }

    @Test
    public void addMultipleLineItemToExistingInvoice() throws Exception {
        Invoice invoice = Invoice.builder().author("Gokul").company("Cognizant").lineItem(new ArrayList<>()).build();
        LineItem lineItem = LineItem.builder().description("project 1").quantity(10).rate(5.4).build();
        LineItem lineItem2 = InvoiceTestHelper.getLineItem2();
        invoice.setLineItem(Arrays.asList(lineItem, lineItem2));
        when(invoiceService.addLineItemToInvoice(any(UUID.class), any())).thenReturn(invoice);
        mockMvc.perform(put("/api/v1/invoice/4fa30ded-c47c-436a-9616-7e3b36be84b3").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(Arrays.asList(lineItem, lineItem2))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.author").value(invoice.getAuthor()))
                .andExpect(jsonPath("$.company").value(invoice.getCompany()))
                .andExpect(jsonPath("$.lineItem", hasSize(2)));

        verify(invoiceService, times(1)).addLineItemToInvoice(any(), any());
    }

    @Test
    public void test_addLineItem_exceptionThrownWhenInvoiceDoesNotExist() throws Exception {
        LineItem lineItem2 = LineItem.builder().description("project 2").quantity(10).rate(4.6).build();
        when(invoiceService.addLineItemToInvoice(any(UUID.class), any())).thenThrow(InvoiceNotFoundException.class);

        mockMvc.perform(put("/api/v1/invoice/4fa30ded-c47c-436a-9616-7e3b36be84b2").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Arrays.asList(lineItem2))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Invoice does not exist"));

    }

    @Test
    public void test_delete_invoice() throws Exception {
        mockMvc.perform(delete("/api/v1/invoice/4fa30ded-c47c-436a-9616-7e3b36be84b2"))
                .andExpect(status().isOk());

        verify(invoiceService, times(1)).deleteInvoice(UUID.fromString("4fa30ded-c47c-436a-9616-7e3b36be84b2"));
    }

}
