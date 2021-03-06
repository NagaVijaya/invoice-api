package com.galvanize.orion.invoicify.TestHelper;

import com.galvanize.orion.invoicify.entities.Invoice;
import com.galvanize.orion.invoicify.entities.LineItem;
import com.galvanize.orion.invoicify.utilities.StatusEnum;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class InvoiceTestHelper {

    public static LineItem getLineItem() {
        return LineItem.builder().description("project 1").quantity(10).rate(BigDecimal.valueOf(5.4)).build();
    }

    public static LineItem getLineItem2() {
        return LineItem.builder().description("project 2").quantity(10).rate(BigDecimal.valueOf(4.6)).build();
    }

    public static LineItem getLineItem3() {
        return LineItem.builder().description("project 3").quantity(10).rate(BigDecimal.valueOf(5.0)).build();
    }

    public static LineItem getLineItem4() {
        return LineItem.builder().description("project 3").quantity(10).rate(BigDecimal.valueOf(10)).build();
    }

    public static List<LineItem> getLineItemListWithTwoLineItem() {
        return Arrays.asList(getLineItem(), getLineItem2());
    }

    public static Invoice getInvoiceWithOneLineItem() {
        return Invoice.builder().author("Gokul").company("Cognizant").lineItems(Collections.singletonList(getLineItem())).build();
    }

    public static Invoice getInvoiceWithTwoLineItem() {
        return Invoice.builder().author("Gokul").company("Cognizant").lineItems(getLineItemListWithTwoLineItem()).build();
    }


    public static Invoice getUnpaidInvoice() {
        return Invoice.builder()
                .id(UUID.fromString("4fa30ded-c47c-436a-9616-7e3b36be84b3"))
                .author("Gokul")
                .company("Cognizant")
                .status(StatusEnum.UNPAID)
                .lineItems(Collections.singletonList(getLineItem()))
                .createdDate(new Date())
                .build();
    }

    public static Invoice getPaidInvoice() {
        return Invoice.builder()
                .id(UUID.fromString("4fa30ded-c47c-436a-9616-7e3b36be84b4"))
                .author("Jenn")
                .company("Cognizant")
                .status(StatusEnum.PAID)
                .lineItems(Collections.singletonList(getLineItem()))
                .build();
    }

    public static Invoice getPaidInvoiceWith1YearOld1() {
        LocalDate createdDateLocal = LocalDate.now();
        createdDateLocal = createdDateLocal.minusYears(1);
        return Invoice.builder()
                .id(UUID.fromString("4fa30ded-c47c-436a-9616-7e3b36be84b3"))
                .author("Gokul")
                .company("Cognizant")
                .status(StatusEnum.PAID)
                .lineItems(Collections.singletonList(getLineItem()))
                .createdDate(Date.from(createdDateLocal.atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .build();
    }

    public static Invoice getUnpaidInvoiceWith1YearOld2() {
        LocalDate createdDateLocal = LocalDate.now();
        createdDateLocal = createdDateLocal.minusYears(1);
        return Invoice.builder()
                .id(UUID.fromString("4fa30ded-c47c-436a-9616-7e3b36be84b3"))
                .author("Gokul")
                .company("Cognizant")
                .status(StatusEnum.UNPAID)
                .lineItems(Collections.singletonList(getLineItem()))
                .createdDate(Date.from(createdDateLocal.atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .build();
    }

    public static Invoice getUnpaidDiscountedInvoice() {
        return Invoice.builder()
                .id(UUID.fromString("4fa30ded-c47c-436a-9616-7e3b36be84b5"))
                .author("Gokul")
                .company("Cognizant")
                .discountPercent(BigDecimal.valueOf(10))
                .status(StatusEnum.UNPAID)
                .totalCost(BigDecimal.valueOf(100))
                .lineItems(Collections.singletonList(getLineItem4()))
                .build();
    }
}
