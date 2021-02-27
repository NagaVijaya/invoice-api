package com.galvanize.orion.invoicify.controller;

import com.galvanize.orion.invoicify.entities.Invoice;
import com.galvanize.orion.invoicify.entities.LineItem;
import com.galvanize.orion.invoicify.exception.InvoiceNotFoundException;
import com.galvanize.orion.invoicify.service.InvoiceService;
import com.galvanize.orion.invoicify.utilities.Constants;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.UUID;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class InvoiceController {

    private InvoiceService invoiceService;

    @PostMapping("/invoice")
    @ResponseStatus(HttpStatus.CREATED)
    public Invoice createInvoice(@RequestBody Invoice invoice){
        return invoiceService.createInvoice(invoice);
    }


    @PutMapping("/invoice/{invoiceId}")
    public Invoice addLineItem(@PathVariable UUID invoiceId, @RequestBody List<LineItem> lineItemList) throws InvoiceNotFoundException {
        return invoiceService.addLineItemToInvoice(invoiceId, lineItemList);
    }


    @GetMapping("/invoices")
    public List<Invoice> getAllInvoices(@RequestParam(defaultValue = Constants.DEFAULT_PAGE_INDEX) Integer page){
        return invoiceService.getAllInvoices(page);
    }
}
