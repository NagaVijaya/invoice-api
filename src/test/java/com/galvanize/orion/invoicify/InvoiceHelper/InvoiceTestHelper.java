package com.galvanize.orion.invoicify.InvoiceHelper;

import com.galvanize.orion.invoicify.entities.Invoice;
import com.galvanize.orion.invoicify.entities.LineItem;
import com.galvanize.orion.invoicify.utilities.StatusEnum;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class InvoiceTestHelper {

    public static LineItem getLineItem() {
        return LineItem.builder().description("project 1").quantity(10).rate(5.4).build();
    }

    public static LineItem getLineItem2() {
        return LineItem.builder().description("project 2").quantity(10).rate(4.6).build();
    }

    public static LineItem getLineItem3() {
        return LineItem.builder().description("project 3").quantity(10).rate(5.0).build();
    }

    public static List<LineItem> getLineItemListWithTwoLineItem() {
        return Arrays.asList(getLineItem(), getLineItem2());
    }

    public static Invoice getInvoiceWithOneLineItem() {
        return Invoice.builder().author("Gokul").company("Cognizant").lineItem(Collections.singletonList(getLineItem())).build();
    }

    public static Invoice getInvoiceWithTwoLineItem() {
        return Invoice.builder().author("Gokul").company("Cognizant").lineItem(getLineItemListWithTwoLineItem()).build();
    }


    public static Invoice getUnpaidInvoice() {
        return Invoice.builder()
                .author("Gokul")
                .company("Cognizant")
                .status(StatusEnum.UNPAID)
                .lineItem(Collections.singletonList(getLineItem()))
                .build();
    }
}
