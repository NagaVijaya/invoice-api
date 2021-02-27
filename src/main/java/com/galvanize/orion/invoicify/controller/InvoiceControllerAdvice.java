package com.galvanize.orion.invoicify.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.galvanize.orion.invoicify.exception.InvoiceNotFoundException;
import com.galvanize.orion.invoicify.exception.InvoicePaidException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class InvoiceControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(InvoiceNotFoundException.class)
    public ResponseEntity<Object> handleInvoiceNotFoundException(InvoiceNotFoundException invoiceNotFoundException, WebRequest webRequest) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode invoiceNotFound = objectMapper.createObjectNode();
        invoiceNotFound.put("message", "Invoice does not exist");
        String messageObject = objectMapper.writeValueAsString(invoiceNotFound);
        return new ResponseEntity<>(messageObject, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvoicePaidException.class)
    public ResponseEntity<Object> handleInvoicePaidException(InvoicePaidException invoicePaidException, WebRequest webRequest) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode invoicePaid = objectMapper.createObjectNode();
        invoicePaid.put("message", "Invoice paid, cannot be modified");
        String messageObject = objectMapper.writeValueAsString(invoicePaid);
        return new ResponseEntity<>(messageObject, HttpStatus.NOT_MODIFIED);
    }
}
