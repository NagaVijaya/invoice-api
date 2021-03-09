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
@Table(name = "company", uniqueConstraints = {@UniqueConstraint(columnNames = {"name"})})
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
    private boolean archived;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "company")
    private List<Invoice> invoices = new ArrayList<>();
}
