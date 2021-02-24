package com.galvanize.orion.invoicify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
public class InvoicifyApplication {

	public static void main(String[] args) {
		SpringApplication.run(InvoicifyApplication.class, args);
	}

}
