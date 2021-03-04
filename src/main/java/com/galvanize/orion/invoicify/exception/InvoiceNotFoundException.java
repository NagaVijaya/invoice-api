package com.galvanize.orion.invoicify.exception;

import com.galvanize.orion.invoicify.utilities.Constants;

public class InvoiceNotFoundException extends Exception{

    public InvoiceNotFoundException() {
        super(Constants.INVOICE_DOES_NOT_EXIST);
    }
}
