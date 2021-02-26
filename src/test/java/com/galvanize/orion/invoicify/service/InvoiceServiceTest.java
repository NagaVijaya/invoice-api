package com.galvanize.orion.invoicify.service;

import com.galvanize.orion.invoicify.entities.Invoice;
import com.galvanize.orion.invoicify.entities.LineItem;
import com.galvanize.orion.invoicify.repository.InvoiceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class InvoiceServiceTest {

    @MockBean
    private InvoiceRepository invoiceRepository;

    @Test
    public void testCreateInvoiceNoLineIem(){
        Invoice invoice = Invoice.builder().author("Gokul").company("Cognizant").lineItem(new ArrayList<>()).build();
        Invoice expectedInvoice = Invoice.builder().author("Gokul").company("Cognizant").totalCost(0).createdDate(new Date()).build();
        InvoiceService invoiceService = new InvoiceService(invoiceRepository);
        when(invoiceRepository.save(any())).thenReturn(expectedInvoice);
        Invoice actualInvoice = invoiceService.createInvoice(invoice);
        assertEquals(expectedInvoice.getAuthor(), actualInvoice.getAuthor());
        assertEquals(expectedInvoice.getCompany(), actualInvoice.getCompany());
        assertEquals(expectedInvoice.getTotalCost(), actualInvoice.getTotalCost());
        assertEquals(expectedInvoice.getCreatedDate(), actualInvoice.getCreatedDate());
        verify(invoiceRepository, times(1)).save(any());
    }

    @Test
    public void testCreateInvoiceMultipleLineIem(){
        LineItem lineItem = LineItem.builder().description("project 1").quantity(10).rate(5.4).build();
        LineItem lineItem2 = LineItem.builder().description("project 2").quantity(10).rate(4.6).build();
        List<LineItem> lineItemList = new ArrayList<>();
        lineItemList.add(lineItem);
        lineItemList.add(lineItem2);
        Invoice invoice = Invoice.builder().author("Gokul").company("Cognizant").lineItem(lineItemList).build();
        Invoice expectedInvoice = Invoice.builder().author("Gokul").company("Cognizant").lineItem(lineItemList).totalCost(100).createdDate(new Date()).build();
        InvoiceService invoiceService = new InvoiceService(invoiceRepository);
        when(invoiceRepository.save(any())).thenReturn(expectedInvoice);
        Invoice actualInvoice = invoiceService.createInvoice(invoice);
        assertEquals(expectedInvoice.getAuthor(), actualInvoice.getAuthor());
        assertEquals(expectedInvoice.getCompany(), actualInvoice.getCompany());
        assertEquals(expectedInvoice.getTotalCost(), actualInvoice.getTotalCost());
        assertEquals(expectedInvoice.getCreatedDate(), actualInvoice.getCreatedDate());
        assertEquals(expectedInvoice.getLineItem().size(), 2);
        assertEquals(expectedInvoice.getLineItem().get(0).getFee(), 54);
        assertEquals(expectedInvoice.getLineItem().get(1).getFee(), 46);
        verify(invoiceRepository, times(1)).save(any());
    }

    @Test
    public void testAddLineItemToExistingInvoice(){


        UUID uid = UUID.fromString("4fa30ded-c47c-436a-9616-7e3b36be84b3");

        LineItem lineItem = LineItem.builder().description("project 1").quantity(10).rate(5.4).fee(54).build();
        LineItem lineItem2 = LineItem.builder().description("project 2").quantity(10).rate(4.6).build();
        List<LineItem> lineItemList = new ArrayList<>();
        lineItemList.add(lineItem);
        Invoice invoice = Invoice.builder().author("Gokul").company("Cognizant").lineItem(lineItemList).build();
        Optional<Invoice> existingInvoice1 = Optional.of(invoice);

        List<LineItem> lineItemList1 = new ArrayList<>();
        lineItemList1.add(lineItem);
        lineItemList1.add(lineItem2);
        Invoice expectedInvoice = Invoice.builder().author("Gokul").company("Cognizant").lineItem(lineItemList1).totalCost(100).createdDate(new Date()).build();

        InvoiceService invoiceService = new InvoiceService(invoiceRepository);
        when(invoiceRepository.save(any())).thenReturn(expectedInvoice);
        when(invoiceRepository.findById(any(UUID.class))).thenReturn(existingInvoice1);

        Invoice actualInvoice = invoiceService.addLineItemToInvoice(uid, lineItem2);

        assertEquals(expectedInvoice.getAuthor(), actualInvoice.getAuthor());
        assertEquals(expectedInvoice.getCompany(), actualInvoice.getCompany());
        assertEquals(expectedInvoice.getTotalCost(), actualInvoice.getTotalCost());
        assertEquals(expectedInvoice.getCreatedDate(), actualInvoice.getCreatedDate());
        assertEquals(expectedInvoice.getLineItem().size(), 2);
        assertEquals(expectedInvoice.getLineItem().get(0).getFee(), 54);
        assertEquals(expectedInvoice.getLineItem().get(1).getFee(), 46);
        verify(invoiceRepository, times(1)).save(any());



    }

    @Test
    public void getAllInvoices_forEmptyList() {
        when(invoiceRepository.findAll()).thenReturn(new ArrayList<>());

        InvoiceService invoiceService = new InvoiceService(invoiceRepository);
        List<Invoice> result = invoiceService.getAllInvoices();
        assertEquals(0, result.size());

        verify(invoiceRepository, times(1)).findAll();
    }

    @Test void getAllInvoices_forSingleInvoice() {
        List<Invoice> invoiceList = new ArrayList<>();
        invoiceList.add(Invoice.builder().build());
        when(invoiceRepository.findAll()).thenReturn(invoiceList);


        InvoiceService invoiceService = new InvoiceService(invoiceRepository);
        List<Invoice> result = invoiceService.getAllInvoices();
        assertEquals(1, result.size());

        verify(invoiceRepository, times(1)).findAll();

    }

    @Test void getAllInvoices_forMultipleInvoice() {
        List<Invoice> invoiceList = new ArrayList<>();
        invoiceList.add(Invoice.builder().author("Peter").build());
        invoiceList.add(Invoice.builder().author("Naga").build());
        when(invoiceRepository.findAll()).thenReturn(invoiceList);


        InvoiceService invoiceService = new InvoiceService(invoiceRepository);
        List<Invoice> result = invoiceService.getAllInvoices();
        assertEquals(2, result.size());
        assertEquals("Peter", result.get(0).getAuthor());
        assertEquals("Naga", result.get(1).getAuthor());

        verify(invoiceRepository, times(1)).findAll();

    }
}
