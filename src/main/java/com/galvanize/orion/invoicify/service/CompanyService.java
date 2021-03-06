package com.galvanize.orion.invoicify.service;

import com.galvanize.orion.invoicify.dto.SimpleCompany;
import com.galvanize.orion.invoicify.entities.Company;
import com.galvanize.orion.invoicify.repository.CompanyRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompanyService {

    private CompanyRepository companyRepository;

    public List<Company> getAllCompanies() {

        return companyRepository.findAllByArchived(false);
    }

    public Company addCompany(Company company) {
        return companyRepository.save(company);
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
}
