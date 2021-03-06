package com.galvanize.orion.invoicify.entities;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(unique = true)
    private String name;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "company")
    private List<Invoice> invoices = new ArrayList<>();
}
