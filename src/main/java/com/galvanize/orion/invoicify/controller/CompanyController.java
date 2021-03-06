package com.galvanize.orion.invoicify.controller;


import com.galvanize.orion.invoicify.entities.Company;
import com.galvanize.orion.invoicify.exception.DuplicateCompanyException;
import com.galvanize.orion.invoicify.service.CompanyService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class CompanyController {

    private CompanyService companyService;

    @GetMapping("/companies")
    public List<String> getAllCompanies() {
        return new ArrayList<>();
    }

    @PostMapping("/company")
    @ResponseStatus(HttpStatus.CREATED)
    public Company addCompany(@RequestBody Company company) throws DuplicateCompanyException {


        return companyService.addCompany(company);
    }
}
