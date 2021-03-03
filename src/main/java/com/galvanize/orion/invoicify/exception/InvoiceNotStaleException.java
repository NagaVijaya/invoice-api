package com.galvanize.orion.invoicify.exception;

import com.galvanize.orion.invoicify.utilities.Constants;

public class InvoiceNotStaleException extends Exception{

    public InvoiceNotStaleException() {
        super(Constants.INVOICE_NOT_STALE);
    }
}
