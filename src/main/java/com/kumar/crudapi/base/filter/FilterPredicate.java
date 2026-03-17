package com.kumar.crudapi.base.filter;

import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class FilterPredicate {

    private final List<SimpleCriteria> conditionsList = new ArrayList<>();

    public FilterPredicate add(SimpleCriteria criteriaDefinition) {
        log.debug("Adding criteria {} ", criteriaDefinition);
        conditionsList.add(criteriaDefinition);
        return this;
    }

    public List<SimpleCriteria> getConditionsList() {
        return conditionsList;
    }

}
