package com.galvanize.orion.invoicify.repository;

import com.galvanize.orion.invoicify.entities.Invoice;
import com.galvanize.orion.invoicify.utilities.StatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {

    public List<Invoice> findByArchivedAndStatusAndCreatedDateBefore(boolean archived, StatusEnum status, Date lastYearDate);

    public Page<Invoice> findAllByArchived(boolean archived, Pageable pageable);

    public List<Invoice> findByCompany_Name(String companyName);
}
