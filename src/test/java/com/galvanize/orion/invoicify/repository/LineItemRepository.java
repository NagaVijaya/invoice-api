package com.galvanize.orion.invoicify.repository;

import com.galvanize.orion.invoicify.entities.LineItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LineItemRepository extends JpaRepository<LineItem, UUID> {
}
