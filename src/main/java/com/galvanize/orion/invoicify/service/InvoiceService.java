package com.galvanize.orion.invoicify.service;

import com.galvanize.orion.invoicify.entities.Invoice;
import com.galvanize.orion.invoicify.entities.LineItem;
import com.galvanize.orion.invoicify.exception.InvoiceNotFoundException;
import com.galvanize.orion.invoicify.repository.InvoiceRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class InvoiceService {

    private InvoiceRepository invoiceRepository;

    public Invoice createInvoice(Invoice invoice) {
        double invoiceTotalCost = 0;
        List<LineItem> lineItemList = invoice.getLineItem();

        //Calculate the cost for each line item and add that cost to invoice
        for(LineItem lineItem: lineItemList){
            double itemCost = lineItem.getQuantity() * lineItem.getRate();
            lineItem.setFee(itemCost);
            invoiceTotalCost += itemCost;
        }
        invoice.setTotalCost(invoiceTotalCost);
        //Set the creation date to the current date.
        invoice.setCreatedDate(new Date());
        return invoiceRepository.save(invoice);
    }

    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    public Invoice addLineItemToInvoice(UUID invoiceId, LineItem lineItem) throws InvoiceNotFoundException {

        Optional<Invoice> invoice = invoiceRepository.findById(invoiceId);
        if (!invoice.isPresent()) {
            throw new InvoiceNotFoundException("Invoice does not exist");
        }

        Invoice existingInvoice = invoice.get();

        double itemCost = lineItem.getQuantity() * lineItem.getRate();
        lineItem.setFee(itemCost);

        existingInvoice.getLineItem().add(lineItem);
        existingInvoice.setTotalCost(existingInvoice.getTotalCost() + itemCost);

        return invoiceRepository.save(existingInvoice);
    }
}
