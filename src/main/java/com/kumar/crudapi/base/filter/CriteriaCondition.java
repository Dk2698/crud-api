package com.kumar.crudapi.base.filter;

import java.util.HashMap;
import java.util.Map;

public enum CriteriaCondition {

    EQ("eq"),
    NE("ne"),

    GT("gt"),
    LT("lt"),
    GTE("gte"),
    LTE("lte"),

    LIKE("like"),
    NOT_LIKE("nlike"),

    STARTS_WITH("startswith"),
    NOT_STARTS_WITH("nstartswith"),

    ENDS_WITH("endswith"),
    NOT_ENDS_WITH("nendswith"),

    IN("in"),
    NOT_IN("nin"),

    BETWEEN("between"),
    NOT_BETWEEN("nbetween"),

    IS_NULL("null"),
    NOT_NULL("nnull"),

    TRUE("true"),
    FALSE("false"),

    CONTAINS("contains"),
    NOT_CONTAINS("ncontains");

    private static final Map<String, CriteriaCondition> BY_LABEL = new HashMap<>();

    static {
        for (CriteriaCondition condition : values()) {
            BY_LABEL.put(condition.label, condition);
        }
    }

    private final String label;

    CriteriaCondition(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static CriteriaCondition valueOfLabel(String label) {
        CriteriaCondition condition = BY_LABEL.get(label.toLowerCase());

        if (condition == null) {
            throw new IllegalArgumentException("Invalid filter condition: " + label);
        }

        return condition;
    }
}
