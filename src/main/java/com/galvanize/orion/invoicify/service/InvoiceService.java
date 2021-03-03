package com.galvanize.orion.invoicify.service;

import com.galvanize.orion.invoicify.entities.Invoice;
import com.galvanize.orion.invoicify.entities.LineItem;
import com.galvanize.orion.invoicify.exception.InvoiceNotFoundException;
import com.galvanize.orion.invoicify.exception.InvoicePaidException;
import com.galvanize.orion.invoicify.repository.InvoiceRepository;
import com.galvanize.orion.invoicify.utilities.Constants;
import com.galvanize.orion.invoicify.utilities.StatusEnum;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class InvoiceService {

    private InvoiceRepository invoiceRepository;

    public Invoice createInvoice(Invoice invoice) {
        BigDecimal invoiceTotalCost = BigDecimal.ZERO;
        List<LineItem> lineItemList = invoice.getLineItems();

        //Calculate the cost for each line item and add that cost to invoice
        for(LineItem lineItem: lineItemList){
            BigDecimal itemCost = lineItem.getRate().multiply(BigDecimal.valueOf(lineItem.getQuantity()));
            lineItem.setFee(itemCost);
            invoiceTotalCost = invoiceTotalCost.add(itemCost);
        }
        invoice.setTotalCost(invoiceTotalCost);
        //Set the creation date to the current date.
        invoice.setCreatedDate(new Date());
        return invoiceRepository.save(invoice);
    }

    public List<Invoice> getAllInvoices(Integer pageNumber) {

        Page<Invoice> page = invoiceRepository.findAll(PageRequest.of(pageNumber,
                Constants.PAGE_SIZE, Sort.by(Sort.Direction.ASC, Constants.ORDER_COLUMN)));

        return page.getContent();
    }

    public Invoice addLineItemToInvoice(UUID invoiceId, List<LineItem> lineItemList) throws InvoiceNotFoundException, InvoicePaidException {

        Invoice existingInvoice = checkValidInvoice(invoiceId);
        List <LineItem> existingInvoiceLineItems = existingInvoice.getLineItems();
        existingInvoiceLineItems.addAll(lineItemList);
        existingInvoice.setLineItems(existingInvoiceLineItems);

        calculateLineItemsTotalCost(existingInvoice);

        existingInvoice.setModifiedDate(new Date());
        return invoiceRepository.save(existingInvoice);
    }

    public Invoice updateInvoice(Invoice invoice) throws InvoicePaidException, InvoiceNotFoundException {
        checkValidInvoice(invoice.getId());
        invoice.setModifiedDate(new Date());
        return invoiceRepository.save(invoice);
    }

    private Invoice checkValidInvoice(UUID invoiceId) throws InvoiceNotFoundException, InvoicePaidException {
        Optional<Invoice> existingOptInvoice = invoiceRepository.findById(invoiceId);
        if (!existingOptInvoice.isPresent()) {
            throw new InvoiceNotFoundException("Invoice does not exist");
        }

        Invoice existingInvoice = existingOptInvoice.get();
        if (StatusEnum.PAID.equals(existingInvoice.getStatus())) {
            throw new InvoicePaidException("Invoice paid, cannot be modified");
        }
        return existingInvoice;
    }

    private Invoice calculateLineItemsTotalCost(Invoice existingInvoice) {
        List<LineItem> lineItemList = existingInvoice.getLineItems();

        BigDecimal invoiceTotalCost = BigDecimal.ZERO;
        for(LineItem lineItem: lineItemList){
            BigDecimal itemCost =  lineItem.getRate().multiply(BigDecimal.valueOf(lineItem.getQuantity()));
            lineItem.setFee(itemCost);
            invoiceTotalCost = invoiceTotalCost.add(itemCost);
        }
        existingInvoice.setTotalCost(invoiceTotalCost);
        return existingInvoice;
    }

}
