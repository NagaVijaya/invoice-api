package com.galvanize.orion.invoicify.exception;

import com.galvanize.orion.invoicify.utilities.Constants;

public class CompanyDoesNotExistException extends Exception{
    public CompanyDoesNotExistException(){
        super(Constants.COMPANY_DOES_NOT_EXIST);
    }
}
