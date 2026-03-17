package com.kumar.crudapi.base.data;

import com.kumar.crudapi.base.filter.FilterPredicate;
import jakarta.persistence.Column;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SqlQueryBuilder {

    public SqlQuery buildQuery(FilterPredicate filter, Pageable pageable, Class<?> entityClass) {
        List<Object> params = new ArrayList<>();
        String whereClause = buildWhereClause(filter, params, entityClass);

        String orderByClause = " ORDER BY ";
        if (pageable.getSort().isEmpty()) {
            orderByClause += "id ASC"; // default
        } else {
            orderByClause += pageable.getSort().stream().map(order -> toColumnName(entityClass, order.getProperty()) + " " + order.getDirection()).collect(Collectors.joining(", "));
        }

        return new SqlQuery(whereClause, orderByClause, params);
    }

    public SqlQuery buildQuery(FilterPredicate filter, Class<?> entityClass) {
        List<Object> params = new ArrayList<>();
        String whereClause = buildWhereClause(filter, params, entityClass);

        // Default ORDER BY id
        String orderByClause = " ORDER BY id ASC";

        return new SqlQuery(whereClause, orderByClause, params);
    }

    public String buildCountQuery(FilterPredicate filter, Class<?> entityClass) {
        List<Object> params = new ArrayList<>();
        String whereClause = buildWhereClause(filter, params, entityClass);

        String table = entityClass.getSimpleName().toLowerCase() + "s";
        return "SELECT COUNT(*) FROM " + table + whereClause;
    }

    public String buildExistsQuery(FilterPredicate filter, Class<?> entityClass) {
        List<Object> params = new ArrayList<>();
        String whereClause = buildWhereClause(filter, params, entityClass);

        String table = entityClass.getSimpleName().toLowerCase() + "s";
        return "SELECT COUNT(*) FROM " + table + whereClause;
    }

    private String buildWhereClause(FilterPredicate filter, List<Object> params, Class<?> entityClass) {

        if (filter == null || filter.getConditionsList().isEmpty()) {
            return " WHERE deleted = false";
        }

        List<String> clauses = filter.getConditionsList().stream().map(cond -> {

            String column = toColumnName(entityClass, cond.field());
            Object value = cond.value();

            switch (cond.condition()) {

                case EQ -> {
                    params.add(value);
                    return column + " = ?";
                }

                case NE -> {
                    params.add(value);
                    return column + " <> ?";
                }

                case GT -> {
                    params.add(value);
                    return column + " > ?";
                }

                case GTE -> {
                    params.add(value);
                    return column + " >= ?";
                }

                case LT -> {
                    params.add(value);
                    return column + " < ?";
                }

                case LTE -> {
                    params.add(value);
                    return column + " <= ?";
                }

                case CONTAINS -> {
                    params.add("%" + value + "%");
                    return column + " LIKE ?";
                }

                case STARTS_WITH -> {
                    params.add(value + "%");
                    return column + " LIKE ?";
                }

                case ENDS_WITH -> {
                    params.add("%" + value);
                    return column + " LIKE ?";
                }

                case IN -> {
                    if (!(value instanceof List<?> values)) {
                        throw new IllegalArgumentException("IN operator requires a list value");
                    }

                    if (values.isEmpty()) {
                        return "1=0"; // no values → always false
                    }

                    String placeholders = String.join(",", Collections.nCopies(values.size(), "?"));

                    params.addAll(values);
                    return column + " IN (" + placeholders + ")";
                }

                case NOT_IN -> {
                    List<?> values = (List<?>) value;

                    if (values == null || values.isEmpty()) {
                        return "1=1";
                    }

                    String placeholders = values.stream()
                            .map(v -> "?")
                            .collect(Collectors.joining(","));

                    params.addAll(values);
                    return column + " NOT IN (" + placeholders + ")";
                }

                case BETWEEN -> {
                    List<?> values = (List<?>) value;

                    if (values == null || values.size() < 2) {
                        throw new IllegalArgumentException("BETWEEN requires 2 values");
                    }

                    params.add(values.get(0));
                    params.add(values.get(1));
                    return column + " BETWEEN ? AND ?";
                }

                case IS_NULL -> {
                    return column + " IS NULL";
                }

                case NOT_NULL -> {
                    return column + " IS NOT NULL";
                }

                case TRUE -> {
                    return column + " = true";
                }

                case FALSE -> {
                    return column + " = false";
                }

                case NOT_CONTAINS -> {
                    params.add("%" + value + "%");
                    return column + " NOT LIKE ?";
                }

                default -> throw new IllegalArgumentException("Unsupported operator: " + cond.condition());
            }

        }).toList();

        return " WHERE deleted = false AND " + String.join(" AND ", clauses);
    }

    public String toColumnName(Class<?> entityClass, String fieldName) {
        try {
            Field field = entityClass.getDeclaredField(fieldName);
            Column col = field.getAnnotation(Column.class);
            if (col != null && !col.name().isEmpty()) {
                return col.name().toLowerCase();
            }
        } catch (NoSuchFieldException ignored) {
        }
        return fieldName.toLowerCase();
    }
}