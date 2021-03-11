package com.galvanize.orion.invoicify.service;

import com.galvanize.orion.invoicify.dto.SimpleCompany;
import com.galvanize.orion.invoicify.entities.Company;
import com.galvanize.orion.invoicify.entities.Invoice;
import com.galvanize.orion.invoicify.exception.CompanyArchivedException;
import com.galvanize.orion.invoicify.exception.CompanyDoesNotExistException;
import com.galvanize.orion.invoicify.exception.DuplicateCompanyException;
import com.galvanize.orion.invoicify.exception.UnpaidInvoiceExistException;
import com.galvanize.orion.invoicify.repository.CompanyRepository;
import com.galvanize.orion.invoicify.utilities.StatusEnum;
import com.galvanize.orion.invoicify.repository.InvoiceRepository;
import com.galvanize.orion.invoicify.utilities.Constants;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompanyService {

    private CompanyRepository companyRepository;
    private InvoiceRepository invoiceRepository;

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

    public Company modifyCompany(String companyId, Company company) throws CompanyDoesNotExistException, DuplicateCompanyException, CompanyArchivedException {
        Optional<Company> existingCompany = companyRepository.findById(UUID.fromString(companyId));
        if (!existingCompany.isPresent()) throw new CompanyDoesNotExistException();

        Company toBeSavedCompany = existingCompany.get();
        if(toBeSavedCompany.isArchived()){
            throw new CompanyArchivedException(Constants.COMPANY_ARCHIVED);
        }

        toBeSavedCompany.setCity(company.getCity());
        toBeSavedCompany.setAddress(company.getAddress());
        toBeSavedCompany.setZipCode(company.getZipCode());
        toBeSavedCompany.setState(company.getState());
        toBeSavedCompany.setName(company.getName());
        try {
            toBeSavedCompany = companyRepository.saveAndFlush(toBeSavedCompany);
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateCompanyException();
        }
        return toBeSavedCompany;
    }

    public List<Invoice> getInvoicesByCompanyName(String name) throws CompanyDoesNotExistException {

        Company company = companyRepository.findByName(name);

        if (null == company) {
            throw new CompanyDoesNotExistException();
        }
        List<Invoice> invoiceList = invoiceRepository.findByCompany_Name(name);

        return invoiceList;
    }

    public Company deleteCompany(String companyId) throws CompanyDoesNotExistException, UnpaidInvoiceExistException {
        Optional<Company> existingCompany = companyRepository.findById(UUID.fromString(companyId));
        if (!existingCompany.isPresent()) throw new CompanyDoesNotExistException();
        List<Invoice> invoiceList = existingCompany.get().getInvoices();
        AtomicBoolean unPaidInvoiceExist = new AtomicBoolean(false);
        invoiceList.forEach(invoice -> {
            if (invoice.getStatus() == StatusEnum.UNPAID)
                unPaidInvoiceExist.set(true);
            else
                invoice.setArchived(true);
        });
        if (unPaidInvoiceExist.get()) {
            throw new UnpaidInvoiceExistException();
        }
        existingCompany.get().setInvoices(invoiceList);
        invoiceRepository.saveAll(invoiceList);
        existingCompany.get().setArchived(true);
        return companyRepository.saveAndFlush(existingCompany.get());
    }
}
