package com.galvanize.orion.invoicify.service;

import com.galvanize.orion.invoicify.InvoiceHelper.InvoiceTestHelper;
import com.galvanize.orion.invoicify.entities.Invoice;
import com.galvanize.orion.invoicify.entities.LineItem;
import com.galvanize.orion.invoicify.exception.InvoiceNotFoundException;
import com.galvanize.orion.invoicify.exception.InvoicePaidException;
import com.galvanize.orion.invoicify.repository.InvoiceRepository;
import com.galvanize.orion.invoicify.utilities.StatusEnum;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class InvoiceServiceTest {

    @MockBean
    private InvoiceRepository invoiceRepository;

    @Test
    public void testCreateInvoiceNoLineIem() {
        Invoice invoice = Invoice.builder()
                                 .author("Gokul")
                                 .company("Cognizant")
                                 .lineItems(new ArrayList<>()).build();
        Invoice expectedInvoice =    Invoice.builder()
                                            .author("Gokul")
                                            .company("Cognizant")
                                            .totalCost(BigDecimal.valueOf(0))
                                            .createdDate(new Date())
                                            .build();
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
    public void testCreateInvoiceMultipleLineIem() {
        LineItem lineItem = LineItem.builder().description("project 1").quantity(10).rate(BigDecimal.valueOf(5.4)).build();
        LineItem lineItem2 = LineItem.builder().description("project 2").quantity(10).rate(BigDecimal.valueOf(4.6)).build();
        List<LineItem> lineItemList = new ArrayList<>();
        lineItemList.add(lineItem);
        lineItemList.add(lineItem2);
        Invoice invoice = Invoice.builder().author("Gokul").company("Cognizant").lineItems(lineItemList).build();
        Invoice expectedInvoice = Invoice.builder()
                                        .author("Gokul")
                                        .company("Cognizant")
                                        .lineItems(lineItemList)
                                        .totalCost(BigDecimal
                                        .valueOf(100))
                                        .createdDate(new Date())
                                        .build();
        InvoiceService invoiceService = new InvoiceService(invoiceRepository);
        when(invoiceRepository.save(any())).thenReturn(expectedInvoice);
        Invoice actualInvoice = invoiceService.createInvoice(invoice);
        assertEquals(expectedInvoice.getAuthor(), actualInvoice.getAuthor());
        assertEquals(expectedInvoice.getCompany(), actualInvoice.getCompany());
        assertEquals(expectedInvoice.getTotalCost(), actualInvoice.getTotalCost());
        assertEquals(expectedInvoice.getCreatedDate(), actualInvoice.getCreatedDate());
        assertEquals(expectedInvoice.getLineItems().size(), 2);
        assertEquals(expectedInvoice.getLineItems().get(0).getFee(), BigDecimal.valueOf(54.0));
        assertEquals(expectedInvoice.getLineItems().get(1).getFee(), BigDecimal.valueOf(46.0));
        verify(invoiceRepository, times(1)).save(any());
    }

    @Test
    public void testAddLineItemToExistingInvoice() throws InvoiceNotFoundException, InvoicePaidException {

        UUID uid = UUID.fromString("4fa30ded-c47c-436a-9616-7e3b36be84b3");

        LineItem lineItem = LineItem.builder()
                                    .description("project 1")
                                    .quantity(10)
                                    .rate(BigDecimal.valueOf(5.4))
                                    .fee(BigDecimal.valueOf(54))
                                    .build();
        LineItem lineItem2 = InvoiceTestHelper.getLineItem2();
        List<LineItem> lineItemList = new ArrayList<>();
        lineItemList.add(lineItem);
        Invoice invoice = Invoice.builder().author("Gokul").company("Cognizant").lineItems(lineItemList).totalCost(BigDecimal.valueOf(0)).build();
        Optional<Invoice> existingInvoice1 = Optional.of(invoice);

        List<LineItem> lineItemList1 = new ArrayList<>();
        lineItemList1.add(lineItem);
        lineItemList1.add(lineItem2);
        Invoice expectedInvoice =    Invoice.builder()
                                            .author("Gokul")
                                            .company("Cognizant")
                                            .lineItems(lineItemList1)
                                            .totalCost(BigDecimal.valueOf(100))
                                            .createdDate(new Date()).build();

        InvoiceService invoiceService = new InvoiceService(invoiceRepository);
        when(invoiceRepository.save(any())).thenReturn(expectedInvoice);
        when(invoiceRepository.findById(any(UUID.class))).thenReturn(existingInvoice1);

        // set up list of invoices to add


        Invoice actualInvoice = invoiceService.addLineItemToInvoice(uid, Arrays.asList(lineItem2));

        assertEquals(expectedInvoice.getAuthor(), actualInvoice.getAuthor());
        assertEquals(expectedInvoice.getCompany(), actualInvoice.getCompany());
        assertEquals(expectedInvoice.getTotalCost(), actualInvoice.getTotalCost());
        assertEquals(expectedInvoice.getCreatedDate(), actualInvoice.getCreatedDate());
        assertEquals(expectedInvoice.getLineItems().size(), 2);
        assertEquals(expectedInvoice.getLineItems().get(0).getFee(), BigDecimal.valueOf(54));
        assertEquals(expectedInvoice.getLineItems().get(1).getFee(), BigDecimal.valueOf(46.0));
        verify(invoiceRepository, times(1)).save(any());
        verify(invoiceRepository, times(1)).findById(any(UUID.class));

    }

    @Test
    public void getAllInvoices_forEmptyList() {
        Page<Invoice> page = new PageImpl<>(new ArrayList<>());
        when(invoiceRepository.findAll(any(PageRequest.class))).thenReturn(page);

        InvoiceService invoiceService = new InvoiceService(invoiceRepository);
        List<Invoice> result = invoiceService.getAllInvoices(1);
        assertEquals(0, result.size());

        verify(invoiceRepository, times(1)).findAll(PageRequest.of(1, 10, Sort.by(Sort.Direction.ASC, "createdDate")));
    }

    @Test
    public void getAllInvoices_forSingleInvoice() {
        List<Invoice> invoiceList = new ArrayList<>();
        invoiceList.add(Invoice.builder().build());
        Page<Invoice> page = new PageImpl<>(invoiceList);
        when(invoiceRepository.findAll(any(PageRequest.class))).thenReturn(page);


        InvoiceService invoiceService = new InvoiceService(invoiceRepository);
        List<Invoice> result = invoiceService.getAllInvoices(1);
        assertEquals(1, result.size());

        verify(invoiceRepository, times(1)).findAll(PageRequest.of(1, 10, Sort.by(Sort.Direction.ASC, "createdDate")));

    }

    @Test
    public void getAllInvoices_forMultipleInvoice() {
        List<Invoice> invoiceList = new ArrayList<>();
        invoiceList.add(Invoice.builder().author("Peter").build());
        invoiceList.add(Invoice.builder().author("Naga").build());
        Page<Invoice> page = new PageImpl<>(invoiceList);
        when(invoiceRepository.findAll(any(PageRequest.class))).thenReturn(page);


        InvoiceService invoiceService = new InvoiceService(invoiceRepository);
        List<Invoice> result = invoiceService.getAllInvoices(1);
        assertEquals(2, result.size());
        assertEquals("Peter", result.get(0).getAuthor());
        assertEquals("Naga", result.get(1).getAuthor());

        verify(invoiceRepository, times(1)).findAll(PageRequest.of(1, 10, Sort.by(Sort.Direction.ASC, "createdDate")));

    }

    @Test
    public void getAll_paginated_sorted_invoices() {
        List<Invoice> invoiceList = new ArrayList<>();
        invoiceList.add(Invoice.builder().author("Peter").build());
        invoiceList.add(Invoice.builder().author("Naga").build());
        Page<Invoice> page = new PageImpl<>(invoiceList);
        when(invoiceRepository.findAll(any(PageRequest.class))).thenReturn(page);


        InvoiceService invoiceService = new InvoiceService(invoiceRepository);
        List<Invoice> result = invoiceService.getAllInvoices(1);
        assertEquals(2, result.size());
        assertEquals("Peter", result.get(0).getAuthor());
        assertEquals("Naga", result.get(1).getAuthor());

        verify(invoiceRepository).findAll(PageRequest.of(1, 10, Sort.by(Sort.Direction.ASC, "createdDate")));

    }

    @Test
    public void testAddLineItemToNonExistingInvoice() throws InvoiceNotFoundException {

        UUID uid = UUID.fromString("4fa30ded-c47c-436a-9616-7e3b36be84b3");

        LineItem lineItem2 = InvoiceTestHelper.getLineItem2();
        Optional<Invoice> existingInvoice = Optional.empty();

        InvoiceService invoiceService = new InvoiceService(invoiceRepository);
        when(invoiceRepository.findById(any(UUID.class))).thenReturn(existingInvoice);

        Exception exception = assertThrows(InvoiceNotFoundException.class, () -> {
            invoiceService.addLineItemToInvoice(uid, Arrays.asList(lineItem2));
        });

        String expectedMessage = "Invoice does not exist";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        verify(invoiceRepository, times(1)).findById(any(UUID.class));

    }

    @Test
    public void testUpdateUnPaidInvoice_toPaidStatus() throws InvoicePaidException, InvoiceNotFoundException {
        Invoice existingInvoice = InvoiceTestHelper.getUnpaidInvoice();
        Optional<Invoice> existingOptInvoice = Optional.of(existingInvoice);
        Invoice modifiedInvoice = InvoiceTestHelper.getPaidInvoice();
        modifiedInvoice.setModifiedDate(new Date());
        when(invoiceRepository.findById(any(UUID.class))).thenReturn(existingOptInvoice);
        when(invoiceRepository.save(any())).thenReturn(modifiedInvoice);
        InvoiceService invoiceService = new InvoiceService(invoiceRepository);

        Invoice actualInvoice = invoiceService.updateInvoice(existingInvoice);

        assertEquals(actualInvoice.getStatus(), StatusEnum.PAID);
        assertNotNull(actualInvoice.getModifiedDate());
        verify(invoiceRepository, times(1)).findById(any(UUID.class));

        verify(invoiceRepository, times(1)).save(any());

    }

    @Test
    public void testUpdatePaidInvoice_throwsException() throws InvoicePaidException, InvoiceNotFoundException {
        Invoice existingInvoice = InvoiceTestHelper.getPaidInvoice();
        Optional<Invoice> existingOptInvoice = Optional.of(existingInvoice);

        when(invoiceRepository.findById(any(UUID.class))).thenReturn(existingOptInvoice);
        InvoiceService invoiceService = new InvoiceService(invoiceRepository);

        Exception exception = assertThrows(InvoicePaidException.class, () -> {
            invoiceService.updateInvoice(existingInvoice);
            ;
        });

        String expectedMessage = "Invoice paid, cannot be modified";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(invoiceRepository, times(1)).findById(any(UUID.class));
    }


}
