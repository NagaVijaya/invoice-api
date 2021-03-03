package com.galvanize.orion.invoicify.exception;

import com.galvanize.orion.invoicify.utilities.Constants;

public class InvoicePaidException extends Exception{

    public InvoicePaidException(){
        super(Constants.INVOICE_PAID_CANNOT_BE_MODIFIED);
    }
}

