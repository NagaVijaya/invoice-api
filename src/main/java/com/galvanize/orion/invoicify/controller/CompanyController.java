package com.galvanize.orion.invoicify.controller;


import com.galvanize.orion.invoicify.dto.SimpleCompany;
import com.galvanize.orion.invoicify.entities.Company;
import com.galvanize.orion.invoicify.entities.Invoice;
import com.galvanize.orion.invoicify.exception.CompanyArchivedException;
import com.galvanize.orion.invoicify.exception.CompanyDoesNotExistException;
import com.galvanize.orion.invoicify.exception.DuplicateCompanyException;
import com.galvanize.orion.invoicify.service.CompanyService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class CompanyController {

    private CompanyService companyService;

    @GetMapping("/companies")
    public List<Company> getAllCompanies() {
        return companyService.getAllCompanies();
    }

    @GetMapping("/companies/simple")
    public List<SimpleCompany> getAllSimpleCompanies() {
        return companyService.getAllSimpleCompanies();
    }

    @PostMapping("/company")
    @ResponseStatus(HttpStatus.CREATED)
    public Company addCompany(@RequestBody Company company) throws DuplicateCompanyException {
        return companyService.addCompany(company);
    }

    @PutMapping("/company/{companyId}")
    public Company modifyCompany(@PathVariable String companyId ,@RequestBody Company company) throws CompanyDoesNotExistException, DuplicateCompanyException, CompanyArchivedException {

        return companyService.modifyCompany(companyId, company);
    }

    @GetMapping("/companies/invoices/{companyName}")
    public List<Invoice> getInvoicesByCompanyName(@PathVariable String companyName) throws CompanyDoesNotExistException {
        return companyService.getInvoicesByCompanyName(companyName);
    }
}
