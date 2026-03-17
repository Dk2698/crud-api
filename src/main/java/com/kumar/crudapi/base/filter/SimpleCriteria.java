package com.kumar.crudapi.base.filter;

public record SimpleCriteria(String field, CriteriaCondition condition, Object value) {
}
