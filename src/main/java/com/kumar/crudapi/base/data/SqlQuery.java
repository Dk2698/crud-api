package com.kumar.crudapi.base;

import java.util.List;

public class SqlQuery {

    private final String whereClause;
    private final List<Object> params;

    public SqlQuery(String whereClause, List<Object> params) {
        this.whereClause = whereClause;
        this.params = params;
    }

    public String getWhereClause() {
        return whereClause;
    }

    public List<Object> getParams() {
        return params;
    }
}