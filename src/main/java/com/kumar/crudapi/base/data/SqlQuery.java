package com.kumar.crudapi.base.data;

import java.util.Collections;
import java.util.List;

public class SqlQuery {

    private final String whereClause;
    private final String orderByClause;
    private final List<Object> params;

    public SqlQuery(String whereClause, String orderByClause, List<Object> params) {
        this.whereClause = whereClause != null ? whereClause : "";
        this.orderByClause = orderByClause != null ? orderByClause : "";
        this.params = params != null ? params : Collections.emptyList();
    }

    public String getWhereClause() {
        return whereClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public List<Object> getParams() {
        return params;
    }
}