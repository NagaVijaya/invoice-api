package com.galvanize.orion.invoicify.exception;

import com.galvanize.orion.invoicify.utilities.Constants;

public class DuplicateCompanyException extends Exception{

    public DuplicateCompanyException(){
        super(Constants.DUPLICATE_COMPANY_MESSAGE);
    }
}
