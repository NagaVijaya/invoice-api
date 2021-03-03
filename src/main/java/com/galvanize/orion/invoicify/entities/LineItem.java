package com.galvanize.orion.invoicify.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class LineItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String description;
    private Integer quantity;
    private BigDecimal rate;
    private BigDecimal fee;
}
