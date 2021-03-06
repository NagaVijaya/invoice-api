package com.galvanize.orion.invoicify.service;

import com.galvanize.orion.invoicify.entities.Company;
import com.galvanize.orion.invoicify.repository.CompanyRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CompanyService {
  
    private CompanyRepository companyRepository;

    public Company addCompany(Company company) {
        return companyRepository.save(company);
    }
}
