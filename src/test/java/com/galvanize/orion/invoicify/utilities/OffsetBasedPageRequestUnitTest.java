package com.galvanize.orion.invoicify.utilities;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class OffsetBasedPageRequestUnitTest {

    @Test
    public void test_constructor(){
        int limit= 0;
        int offset = -1;
        assertThrows(IllegalArgumentException.class , () ->{
            new OffsetBasedPageRequest(limit,offset);
        });
    }

}
