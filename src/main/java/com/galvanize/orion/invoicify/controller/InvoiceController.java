package com.galvanize.orion.invoicify.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class InvoiceController {

    @PostMapping("/invoice")
    @ResponseStatus(HttpStatus.CREATED)
    public void createInvoice(){

    }

}
