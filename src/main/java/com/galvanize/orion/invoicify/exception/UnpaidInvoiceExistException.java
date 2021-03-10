package com.galvanize.orion.invoicify.exception;

import com.galvanize.orion.invoicify.utilities.Constants;

public class UnpaidInvoiceExistException extends Exception{
    public UnpaidInvoiceExistException(){
        super(Constants.UNPAID_INVOICE_EXIST_CAN_NOT_DELETE_COMPANY);
    }
}
