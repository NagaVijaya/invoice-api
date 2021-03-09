package com.galvanize.orion.invoicify.exception;

import com.galvanize.orion.invoicify.utilities.Constants;

public class CompanyDoesNotExist extends Exception{
    public CompanyDoesNotExist(){
        super(Constants.COMPANY_DOES_NOT_EXIST);
    }
}
