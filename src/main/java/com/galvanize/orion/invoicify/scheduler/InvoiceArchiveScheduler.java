package com.galvanize.orion.invoicify.scheduler;

import com.galvanize.orion.invoicify.service.InvoiceService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class InvoiceArchiveScheduler {

    private InvoiceService invoiceService;

    //Schedule the jon to run @ 12 AM everyday
    //Cron format = sec, min, hour, day of month, mpnth, day of week, year
    @Scheduled(cron = "0 0 0 ? * * *")
    public void run() {
        invoiceService.archiveInvoices();
    }
}
