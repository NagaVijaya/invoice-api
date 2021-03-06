package com.galvanize.orion.invoicify.service;

import com.galvanize.orion.invoicify.entities.Company;
import com.galvanize.orion.invoicify.exception.DuplicateCompanyException;
import com.galvanize.orion.invoicify.repository.CompanyRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CompanyService {
    @Autowired
    private CompanyRepository companyRepository;

    public Company addCompany(Company company) throws DuplicateCompanyException {
        Company newCompany = null;
        try {
            newCompany = companyRepository.save(company);
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateCompanyException();
        }

        return newCompany;
    }
}
