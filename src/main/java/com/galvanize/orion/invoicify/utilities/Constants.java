package com.galvanize.orion.invoicify.utilities;

import org.apache.commons.lang3.Range;

import java.math.BigDecimal;

public class Constants {
    public static final String DEFAULT_PAGE_INDEX = "0";
    public static final int PAGE_SIZE = 10;
    public static final String ORDER_COLUMN = "createdDate";
    public static final String INVOICE_NOT_STALE = "Invoice is less than 1 year old, can't delete!";
    public static final String MESSAGE = "message";
    public static final String INVOICE_PAID_CANNOT_BE_MODIFIED = "Invoice paid, cannot be modified";
    public static final String INVOICE_DOES_NOT_EXIST = "Invoice does not exist";
    public static final boolean FALSE = false;
    public static final String DUPLICATE_COMPANY_MESSAGE = "Company already exist" ;
    public static final BigDecimal ONE_HUNDRED_PERCENT = BigDecimal.valueOf(100.00);
    public static final Range<BigDecimal> DISCOUNT_RANGE = Range.between(BigDecimal.ZERO, ONE_HUNDRED_PERCENT);
    public static final String DISCOUNT_OUT_OF_BOUNDS = "Discount percent out of bounds";
    public static final String COMPANY_DOES_NOT_EXIST = "Company does not exist";
}
