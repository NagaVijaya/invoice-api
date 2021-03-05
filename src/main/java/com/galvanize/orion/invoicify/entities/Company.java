package com.galvanize.orion.invoicify.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@Builder
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private String name;
    private String address;
    private String city;
    private String state;
}
