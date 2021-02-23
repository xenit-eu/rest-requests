package eu.xenit.restrequests.impl;

import eu.xenit.restrequests.api.filters.ReactiveRestClientFilter;
import java.util.Collection;

public class FilterProcessor {


    private final Collection<? extends ReactiveRestClientFilter> filters;

    public FilterProcessor(Collection<? extends ReactiveRestClientFilter> filters) {
        this.filters = filters;
    }


}
