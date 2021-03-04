package com.galvanize.orion.invoicify.scheduler;

import com.galvanize.orion.invoicify.service.InvoiceService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.mockito.Mockito.*;

@SpringJUnitConfig(InvoiceArchiveScheduler.class)
public class InvoiceArchiveSchedulerUnitTest {

    @MockBean
    InvoiceService invoiceService;

    @Test
    public void testInvoiceArchive(){

        doNothing().when(invoiceService).archiveInvoices();
        InvoiceArchiveScheduler invoiceArchiveScheduler = new InvoiceArchiveScheduler(invoiceService);
        invoiceArchiveScheduler.run();
        verify(invoiceService, times(1)).archiveInvoices();

    }

}
