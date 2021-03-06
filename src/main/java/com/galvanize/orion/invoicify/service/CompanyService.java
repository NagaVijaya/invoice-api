package com.galvanize.orion.invoicify.service;

import com.galvanize.orion.invoicify.dto.SimpleCompany;
import com.galvanize.orion.invoicify.entities.Company;
import com.galvanize.orion.invoicify.exception.CompanyDoesNotExist;
import com.galvanize.orion.invoicify.exception.DuplicateCompanyException;
import com.galvanize.orion.invoicify.repository.CompanyRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompanyService {

    private CompanyRepository companyRepository;

    public List<Company> getAllCompanies() {

        return companyRepository.findAllByArchived(false);
    }

    public Company addCompany(Company company) throws DuplicateCompanyException {
        Company newCompany = null;

        try {
            newCompany = companyRepository.saveAndFlush(company);
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateCompanyException();
        }

        return newCompany;
    }

    public List<SimpleCompany> getAllSimpleCompanies() {
        List<Company> companyList = companyRepository.findAllByArchived(false);
        List<SimpleCompany> simpleCompanies = companyList.stream()
                .map(company -> SimpleCompany.builder()
                                .name(company.getName())
                                .city(company.getCity())
                                .state(company.getState())
                                .build())
                .collect(Collectors.toList());

        return simpleCompanies;
    }

    public Company modifyCompany(String companyId ,Company company) throws CompanyDoesNotExist {
        Optional<Company> existingCompany = companyRepository.findById(UUID.fromString(companyId));
       if(!existingCompany.isPresent()) throw new CompanyDoesNotExist();
        company.setId(UUID.fromString(companyId));
        return companyRepository.saveAndFlush(company);
    }

    public Company deleteCompany(String companyId) throws CompanyDoesNotExist {
        Optional<Company> existingCompany = companyRepository.findById(UUID.fromString(companyId));
        if(!existingCompany.isPresent()) throw new CompanyDoesNotExist();
        existingCompany.get().setArchived(true);
        return companyRepository.saveAndFlush(existingCompany.get());
    }
}
