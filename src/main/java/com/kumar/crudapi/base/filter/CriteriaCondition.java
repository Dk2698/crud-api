package com.kumar.crudapi.base.filter;

import java.util.HashMap;
import java.util.Map;

public enum CriteriaCondition {

    EQUALS("eq"),
    NOT_EQUALS("ne"),
    LESS_THAN("lt"),
    GREATER_THAN("gt"),
    LESS_THAN_EQUAL("lte"),
    GREATER_THAN_EQUAL("gte"),
    IN("in"),
    NOT_IN("nin"),
    CONTAINS("contains"),
    NOT_CONTAINS("ncontains"),
    BETWEEN("between"),
    NOT_BETWEEN("nbetween"),
    NULL("null"),
    NOT_NULL("nnull"),
    STARTS_WITH("startswith"),
    NOT_STARTS_WITH("nstartswith"),
    ENDS_WITH("endswith"),
    NOT_ENDS_WITH("nendswith");

    private static final Map<String, CriteriaCondition> BY_LABEL = new HashMap<>();

    static {
        for (CriteriaCondition e : values()) {
            BY_LABEL.put(e.label, e);
        }
    }

    //( "containss" ), ( "ncontainss" ),  ( "startswiths" ), ( "nstartswiths" ), , ( "endswiths" ), ( "nendswiths")
    public final String label;

    CriteriaCondition(String label) {
        this.label = label;
    }

    public static CriteriaCondition valueOfLabel(String label) {
        return BY_LABEL.get(label);
    }
}
