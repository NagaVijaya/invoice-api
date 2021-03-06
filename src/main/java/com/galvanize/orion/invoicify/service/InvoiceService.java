package com.galvanize.orion.invoicify.service;

import com.galvanize.orion.invoicify.entities.Company;
import com.galvanize.orion.invoicify.entities.Invoice;
import com.galvanize.orion.invoicify.entities.LineItem;
import com.galvanize.orion.invoicify.exception.CompanyDoesNotExistException;
import com.galvanize.orion.invoicify.exception.InvoiceNotFoundException;
import com.galvanize.orion.invoicify.exception.InvoiceNotStaleException;
import com.galvanize.orion.invoicify.exception.InvoicePaidException;
import com.galvanize.orion.invoicify.repository.CompanyRepository;
import com.galvanize.orion.invoicify.repository.InvoiceRepository;
import com.galvanize.orion.invoicify.utilities.Constants;
import com.galvanize.orion.invoicify.utilities.StatusEnum;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.galvanize.orion.invoicify.utilities.Constants.*;

@Service
@AllArgsConstructor
public class InvoiceService {

    private InvoiceRepository invoiceRepository;
    private CompanyRepository companyRepository;

    public Invoice createInvoice(Invoice invoice) throws IllegalAccessException, CompanyDoesNotExistException {

        Company existingCompany = companyRepository.findByName(invoice.getCompany().getName());
        if(existingCompany == null){
            throw new CompanyDoesNotExistException();
        }

        //Calculate the cost for each line item and add that cost to invoice
        calculateLineItemsTotalCost(invoice);
        //Set the creation date to the current date.
        invoice.setCreatedDate(new Date());
        return invoiceRepository.save(invoice);
    }

    public List<Invoice> getAllInvoices(Integer pageNumber) {

        Page<Invoice> page = invoiceRepository.findAllByArchived(Constants.FALSE, PageRequest.of(pageNumber,
                Constants.PAGE_SIZE, Sort.by(Sort.Direction.ASC, Constants.ORDER_COLUMN)));

        return page.getContent();
    }

    public Invoice addLineItemToInvoice(UUID invoiceId, List<LineItem> lineItemList) throws InvoiceNotFoundException, InvoicePaidException, IllegalAccessException {

        Invoice existingInvoice = checkValidInvoice(invoiceId);
        List <LineItem> existingInvoiceLineItems = existingInvoice.getLineItems();
        existingInvoiceLineItems.addAll(lineItemList);
        existingInvoice.setLineItems(existingInvoiceLineItems);

        calculateLineItemsTotalCost(existingInvoice);

        existingInvoice.setModifiedDate(new Date());
        return invoiceRepository.save(existingInvoice);
    }

    public Invoice updateInvoice(Invoice invoice) throws InvoicePaidException, InvoiceNotFoundException, IllegalAccessException {
        Invoice invoiceToBeModified = checkValidInvoice(invoice.getId());
        calculateLineItemsTotalCost(invoice);
        invoiceToBeModified.setTotalCost(invoice.getTotalCost());
        invoiceToBeModified.setModifiedDate(new Date());
        invoiceToBeModified.setStatus(invoice.getStatus());
        return invoiceRepository.save(invoiceToBeModified);
    }

    public void deleteInvoice(UUID invoiceId) throws InvoiceNotStaleException, InvoiceNotFoundException {
        Optional<Invoice> optionalInvoice = invoiceRepository.findById(invoiceId);

        if (!optionalInvoice.isPresent()) {
            throw new InvoiceNotFoundException();
        }

        Invoice invoice = optionalInvoice.get();
        LocalDate localYearBackDate = LocalDate.now().minusYears(1);

        LocalDate invoiceLocalDate = invoice.getCreatedDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        if(invoiceLocalDate.isAfter(localYearBackDate)){
            throw new InvoiceNotStaleException();
        }

        invoiceRepository.deleteById(invoiceId);
    }

    public void archiveInvoices() {

        LocalDate createdDateLocal = LocalDate.now().minusYears(1);
        Date lastYearDate = Date.from(createdDateLocal.atStartOfDay(ZoneId.systemDefault()).toInstant());

        List<Invoice> invoiceToArchive = invoiceRepository.findByArchivedAndStatusAndCreatedDateBefore(false, StatusEnum.PAID, lastYearDate);
        invoiceToArchive.forEach(invoice -> invoice.setArchived(true));
        invoiceRepository.saveAll(invoiceToArchive);
    }

    private Invoice checkValidInvoice(UUID invoiceId) throws InvoiceNotFoundException, InvoicePaidException {
        Optional<Invoice> existingOptInvoice = invoiceRepository.findById(invoiceId);
        if (!existingOptInvoice.isPresent()) {
            throw new InvoiceNotFoundException();
        }

        Invoice existingInvoice = existingOptInvoice.get();
        if (StatusEnum.PAID.equals(existingInvoice.getStatus())) {
            throw new InvoicePaidException();
        }
        return existingInvoice;
    }

    private Invoice calculateLineItemsTotalCost(Invoice existingInvoice) throws IllegalAccessException {

        List<LineItem> lineItemList = existingInvoice.getLineItems();

        BigDecimal invoiceTotalCost = BigDecimal.ZERO;
        for(LineItem lineItem: lineItemList){
            BigDecimal itemCost =  lineItem.getRate().multiply(BigDecimal.valueOf(lineItem.getQuantity()));
            lineItem.setFee(itemCost);
            invoiceTotalCost = invoiceTotalCost.add(itemCost);
        }
        if (existingInvoice.getDiscountPercent() != null) {
            if (!DISCOUNT_RANGE.contains(existingInvoice.getDiscountPercent()))
                throw new IllegalArgumentException(DISCOUNT_OUT_OF_BOUNDS);
            BigDecimal discountAmount = existingInvoice.getDiscountPercent().divide(BigDecimal.valueOf(100)).multiply(invoiceTotalCost);
            invoiceTotalCost = invoiceTotalCost.subtract(discountAmount);
        }

        invoiceTotalCost = invoiceTotalCost.setScale(2, RoundingMode.CEILING);

        existingInvoice.setTotalCost(invoiceTotalCost);
        return existingInvoice;
    }
}
