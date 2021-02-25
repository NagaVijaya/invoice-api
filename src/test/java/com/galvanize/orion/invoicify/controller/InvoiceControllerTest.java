package com.galvanize.orion.invoicify.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.orion.invoicify.entities.Invoice;
import com.galvanize.orion.invoicify.entities.LineItem;
import com.galvanize.orion.invoicify.service.InvoiceService;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = InvoiceController.class)
public class InvoiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InvoiceService invoiceService;

    @Autowired
    private ObjectMapper mapper;


    @Test
    public void createInvoiceCallsInvoiceService() throws Exception {
        Invoice invoice = Invoice.builder().author("Gokul").company("Cognizant").lineItem(new ArrayList<>()).build();
        when(invoiceService.createInvoice(any())).thenReturn(invoice);
        mockMvc.perform(post("/api/v1/invoice").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(invoice)))
                .andExpect(status().isCreated());
        verify(invoiceService, times(1)).createInvoice(any());
    }

    @Test
    public void addLineItemToExistingInvoice() throws Exception {
        Invoice invoice = Invoice.builder().author("Gokul").company("Cognizant").lineItem(new ArrayList<>()).build();
        LineItem lineItem = LineItem.builder().description("project 1").quantity(10).rate(5.4).build();
        invoice.setLineItem(Collections.singletonList(lineItem));
        when(invoiceService.addLineItemToInvoice(any(UUID.class), any())).thenReturn(invoice);
        mockMvc.perform(put("/api/v1/invoice/4fa30ded-c47c-436a-9616-7e3b36be84b3").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(lineItem)))
                .andExpect(status().isOk());

        verify(invoiceService, times(1)).addLineItemToInvoice(any(), any());
    }
}