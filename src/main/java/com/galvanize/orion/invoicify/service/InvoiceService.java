package com.galvanize.orion.invoicify.service;

import com.galvanize.orion.invoicify.entities.Invoice;
import com.galvanize.orion.invoicify.entities.LineItem;
import com.galvanize.orion.invoicify.exception.InvoiceNotFoundException;
import com.galvanize.orion.invoicify.exception.InvoiceNotStaleException;
import com.galvanize.orion.invoicify.exception.InvoicePaidException;
import com.galvanize.orion.invoicify.repository.InvoiceRepository;
import com.galvanize.orion.invoicify.utilities.Constants;
import com.galvanize.orion.invoicify.utilities.StatusEnum;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
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

    public List<Invoice> getAllInvoices(Integer pageNumber) {

        Page<Invoice> page = invoiceRepository.findAll(PageRequest.of(pageNumber,
                Constants.PAGE_SIZE, Sort.by(Sort.Direction.ASC, Constants.ORDER_COLUMN)));

        return page.getContent();
    }

    public Invoice addLineItemToInvoice(UUID invoiceId, List<LineItem> lineItemList) throws InvoiceNotFoundException {

        Optional<Invoice> invoice = invoiceRepository.findById(invoiceId);
        if (!invoice.isPresent()) {
            throw new InvoiceNotFoundException("Invoice does not exist");
        }

        Invoice existingInvoice = invoice.get();

        double invoiceTotalCost = existingInvoice.getTotalCost();
        for(LineItem lineItem: lineItemList){
            double itemCost = lineItem.getQuantity() * lineItem.getRate();
            lineItem.setFee(itemCost);
            invoiceTotalCost += itemCost;
            existingInvoice.getLineItem().add(lineItem);
        }

        existingInvoice.setTotalCost(invoiceTotalCost);

        return invoiceRepository.save(existingInvoice);
    }

    public Invoice updateInvoice(Invoice invoice) throws InvoicePaidException, InvoiceNotFoundException {
        Optional<Invoice> existingOptInvoice = invoiceRepository.findById(invoice.getId());
        if (!existingOptInvoice.isPresent()) {
            throw new InvoiceNotFoundException("Invoice does not exist");
        }

        Invoice existingInvoice = existingOptInvoice.get();
        if(existingInvoice.getStatus().equals(StatusEnum.PAID)){
            throw new InvoicePaidException("Invoice paid, cannot be modified");
        }

        return invoiceRepository.save(invoice);
    }

    public void deleteInvoice(UUID invoiceId) throws InvoiceNotStaleException {
        Invoice invoice = invoiceRepository.findById(invoiceId).get();
        LocalDate localYearBackDate = LocalDate.now().minusYears(1);

        LocalDate invoiceLocalDate = invoice.getCreatedDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        if(invoiceLocalDate.isAfter(localYearBackDate)){
            throw new InvoiceNotStaleException();
        }

        invoiceRepository.deleteById(invoiceId);
    }
}
