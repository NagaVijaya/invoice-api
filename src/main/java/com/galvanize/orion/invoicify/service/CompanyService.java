package com.galvanize.orion.invoicify.service;

import com.galvanize.orion.invoicify.entities.Company;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import com.galvanize.orion.invoicify.repository.CompanyRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CompanyService {

    private CompanyRepository companyRepository;

    public List<Company> getAllCompanies() {

        return new ArrayList<>();
    }

    public Company addCompany(Company company) {
        return companyRepository.save(company);
    }
}
