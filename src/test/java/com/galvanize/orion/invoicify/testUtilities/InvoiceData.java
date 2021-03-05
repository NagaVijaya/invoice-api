package com.galvanize.orion.invoicify.testUtilities;

import com.galvanize.orion.invoicify.entities.Invoice;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InvoiceData {

    public static List<Invoice> GenerateInvoices() {
        List<Invoice> invoiceList = new ArrayList<>();

        LocalDate startDate = LocalDate.now();

        for (int invoiceIndex = 0; invoiceIndex < 21; invoiceIndex++) {

            LocalDate currentLocalDate = startDate.minusDays(invoiceIndex);
            Date currentDate = Date.from(currentLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

            Invoice invoice = Invoice.builder()
                    .author(String.format("Author%02d", invoiceIndex))
                    .createdDate(currentDate)
                    .build();

            invoiceList.add(invoice);

        }

        return invoiceList;
    }

    public static List<Invoice> GenerateInvoicesWithArchived() {
        List<Invoice> invoiceList = new ArrayList<>();

        LocalDate startDate = LocalDate.now();

        for (int invoiceIndex = 0; invoiceIndex < 10; invoiceIndex++) {

            LocalDate currentLocalDate = startDate.minusDays(invoiceIndex);
            Date currentDate = Date.from(currentLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

            Invoice invoice = Invoice.builder()
                    .author(String.format("Author%02d", invoiceIndex))
                    .createdDate(currentDate)
                    .build();
            if(invoiceIndex == 7)
                invoice.setArchived(true);

            invoiceList.add(invoice);

        }

        return invoiceList;
    }

}
