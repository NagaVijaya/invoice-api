package com.galvanize.orion.invoicify.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LineItem {

    @Id
    private Long id;
    private String description;
    private int quantity;
    private double rate;
    private double fee;
}
