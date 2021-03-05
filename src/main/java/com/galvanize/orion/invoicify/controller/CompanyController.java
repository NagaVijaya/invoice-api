package com.galvanize.orion.invoicify.controller;


import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class CompanyController {

    @GetMapping("/companies")
    public List<String> getAllCompanies() {
        return new ArrayList<>();
    }
}
