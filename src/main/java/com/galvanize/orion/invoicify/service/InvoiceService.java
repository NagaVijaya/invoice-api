package com.galvanize.orion.invoicify.service;

import com.galvanize.orion.invoicify.entities.Invoice;
import com.galvanize.orion.invoicify.entities.LineItem;
import com.galvanize.orion.invoicify.repository.InvoiceRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.sound.sampled.Line;
import java.util.Date;
import java.util.List;
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

    public Invoice addLineItemToInvoice(UUID invoiceId, LineItem lineItem) {
        return null;
    }
}
