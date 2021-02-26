package com.galvanize.orion.invoicify.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.orion.invoicify.entities.Invoice;
import com.galvanize.orion.invoicify.entities.LineItem;
import com.galvanize.orion.invoicify.service.InvoiceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        mockMvc.perform(post("/api/v1/invoice").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(invoice)));
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
    public void test_getAllInvoices_handlesOffsetParameter() throws Exception {
        List<Invoice> invoiceList = new ArrayList<>();
        invoiceList.add(Invoice.builder()
                .author("Peter")
                .build());
        invoiceList.add(Invoice.builder()
                .author("Naga")
                .build());
        when(invoiceService.getAllInvoices(10)).thenReturn(invoiceList);

        mockMvc.perform(get("/api/v1/invoices?offset=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].author").value("Peter"))
                .andExpect(jsonPath("$[1].author").value("Naga"));
        verify(invoiceService, times(1)).getAllInvoices(10);
    }



}
