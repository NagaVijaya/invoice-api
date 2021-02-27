package com.galvanize.orion.invoicify.testUtilities;

import org.mockito.ArgumentMatcher;
import org.springframework.data.domain.PageRequest;

public class PageRequestMatcher implements ArgumentMatcher<PageRequest> {

    public PageRequestMatcher(PageRequest left) {
        this.left = left;
    }

    private PageRequest left;

    @Override
    public boolean matches(PageRequest right) {
        if (right.getPageSize() != 10)
            return false;

        if (right.getPageNumber() != left.getPageNumber())
            return false;

//        Sort sortRight = right.getSort();
//        if (sortRight.getOrderFor("0").getDirection() != Sort.Direction.ASC)
//            return false;
//        if (sortRight.getOrderFor("0").getProperty() != "createdDate")
//            return false;

        return true;
    }
}
