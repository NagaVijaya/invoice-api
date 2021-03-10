package com.galvanize.orion.invoicify.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.galvanize.orion.invoicify.exception.CompanyArchivedException;
import com.galvanize.orion.invoicify.exception.CompanyDoesNotExistException;
import com.galvanize.orion.invoicify.exception.DuplicateCompanyException;
import com.galvanize.orion.invoicify.exception.InvoiceNotFoundException;
import com.galvanize.orion.invoicify.exception.UnpaidInvoiceExistException;
import com.galvanize.orion.invoicify.utilities.Constants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class CompanyControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(DuplicateCompanyException.class)
    public ResponseEntity<Object> handleDuplicateCompanyException(DuplicateCompanyException duplicateCompanyException, WebRequest webRequest) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode duplicateCompany = objectMapper.createObjectNode();
        duplicateCompany.put(Constants.MESSAGE, duplicateCompanyException.getMessage());
        String messageObject = objectMapper.writeValueAsString(duplicateCompany);
        return new ResponseEntity<>(messageObject, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(CompanyDoesNotExistException.class)
    public ResponseEntity<Object> handleCompanyDoesNotExistException(CompanyDoesNotExistException companyDoesNotExistException, WebRequest webRequest) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode companyDoesNotExistObj = objectMapper.createObjectNode();
        companyDoesNotExistObj.put(Constants.MESSAGE, companyDoesNotExistException.getMessage());
        String messageObject = objectMapper.writeValueAsString(companyDoesNotExistObj);
        return new ResponseEntity<>(messageObject, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnpaidInvoiceExistException.class)
    public ResponseEntity<Object> handleUnpaidInvoiceExistException(UnpaidInvoiceExistException unpaidInvoiceExistException, WebRequest webRequest) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode unpaidInvoiceExist = objectMapper.createObjectNode();
        unpaidInvoiceExist.put(Constants.MESSAGE, unpaidInvoiceExistException.getMessage());
        String messageObject = objectMapper.writeValueAsString(unpaidInvoiceExist);
        return new ResponseEntity<>(messageObject, HttpStatus.NOT_ACCEPTABLE);
    }


    @ExceptionHandler(CompanyArchivedException.class)
    public ResponseEntity<Object> handleCompanyArchivedExceptionException(CompanyArchivedException companyArchivedException, WebRequest webRequest) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode companyDoesNotExistObj = objectMapper.createObjectNode();
        companyDoesNotExistObj.put(Constants.MESSAGE, companyArchivedException.getMessage());
        String messageObject = objectMapper.writeValueAsString(companyDoesNotExistObj);
        return new ResponseEntity<>(messageObject, HttpStatus.BAD_REQUEST);
    }
}
