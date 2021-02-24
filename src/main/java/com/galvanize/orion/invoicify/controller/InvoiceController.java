package com.galvanize.orion.invoicify.controller;

import com.galvanize.orion.invoicify.entities.Invoice;
import com.galvanize.orion.invoicify.service.InvoiceService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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

    @GetMapping("/invoices")
    public List<String> getAllInvoices(){
        return invoiceService.getAllInvoices();
    }
}
