package com.kumar.crudapi.base.data;

import com.kumar.crudapi.base.filter.FilterPredicate;
import jakarta.persistence.Column;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
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
            return " WHERE deleted = false"; // soft delete support
        }

        List<String> clauses = filter.getConditionsList().stream().map(cond -> {
            String clause;
            switch (cond.condition()) {
                case EQUALS -> clause = toColumnName(entityClass, cond.field()) + " = ?";
                case NOT_EQUALS -> clause = toColumnName(entityClass, cond.field()) + " <> ?";
                case GREATER_THAN -> clause = toColumnName(entityClass, cond.field()) + " > ?";
                case GREATER_THAN_EQUAL -> clause = toColumnName(entityClass, cond.field()) + " >= ?";
                case LESS_THAN -> clause = toColumnName(entityClass, cond.field()) + " < ?";
                case LESS_THAN_EQUAL -> clause = toColumnName(entityClass, cond.field()) + " <= ?";
                case CONTAINS -> clause = toColumnName(entityClass, cond.field()) + " LIKE ?";
                case IN -> {
//                    List<?> values = (List<?>) cond.field();//TODO
                    List<Object> values = new ArrayList<>();
                    String placeholders = values.stream().map(v -> "?").collect(Collectors.joining(","));
                    clause = toColumnName(entityClass, cond.field()) + " IN (" + placeholders + ")";
                    params.addAll(values);
                    return clause;
                }
                default -> throw new IllegalArgumentException("Unsupported operator: " + cond.condition());
            }
            params.add(cond.value());
            return clause;
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