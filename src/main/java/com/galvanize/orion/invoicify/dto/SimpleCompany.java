package com.galvanize.orion.invoicify.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SimpleCompany {
    private String name;
    private String city;
    private String state;
}
