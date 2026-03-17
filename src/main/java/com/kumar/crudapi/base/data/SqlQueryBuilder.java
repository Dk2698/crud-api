package com.kumar.crudapi.base;

import com.kumar.crudapi.base.data.SqlQuery;
import com.kumar.crudapi.base.filter.FilterPredicate;
import com.kumar.crudapi.base.filter.SimpleCriteria;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class SqlQueryBuilder {

    private final ConversionService conversionService;

    public SqlQueryBuilder(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    public SqlQuery buildQuery(FilterPredicate predicate, Class<?> entityClass) {

        StringBuilder sql = new StringBuilder(" WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        for (SimpleCriteria criteria : predicate.getConditionsList()) {

            String field = criteria.field();
            Class<?> targetClass = isAssignable(entityClass, field);

            if (targetClass == null) {
                continue;
            }

            List<String> value = criteria.value();

            switch (criteria.condition()) {

                case NULL:
                    sql.append(" AND ").append(field).append(" IS NULL ");
                    break;

                case NOT_NULL:
                    sql.append(" AND ").append(field).append(" IS NOT NULL ");
                    break;

                case NOT_EQUALS:
                    sql.append(" AND ").append(field).append(" <> ? ");
                    params.add(convert(value.get(0), targetClass));
                    break;

                case CONTAINS:
                    sql.append(" AND LOWER(").append(field).append(") LIKE LOWER(?) ");
                    params.add("%" + value.get(0) + "%");
                    break;

                case STARTS_WITH:
                    sql.append(" AND LOWER(").append(field).append(") LIKE LOWER(?) ");
                    params.add(value.get(0) + "%");
                    break;

                case ENDS_WITH:
                    sql.append(" AND LOWER(").append(field).append(") LIKE LOWER(?) ");
                    params.add("%" + value.get(0));
                    break;

                case IN:
                    sql.append(" AND ").append(field).append(" IN (");
                    appendPlaceholders(sql, value.size());
                    sql.append(")");
                    params.addAll(convertList(value, targetClass));
                    break;

                case GREATER_THAN:
                    sql.append(" AND ").append(field).append(" > ? ");
                    params.add(convert(value.get(0), targetClass));
                    break;

                case GREATER_THAN_EQUAL:
                    sql.append(" AND ").append(field).append(" >= ? ");
                    params.add(convert(value.get(0), targetClass));
                    break;

                case LESS_THAN:
                    sql.append(" AND ").append(field).append(" < ? ");
                    params.add(convert(value.get(0), targetClass));
                    break;

                case LESS_THAN_EQUAL:
                    sql.append(" AND ").append(field).append(" <= ? ");
                    params.add(convert(value.get(0), targetClass));
                    break;

                case BETWEEN:
                    sql.append(" AND ").append(field).append(" BETWEEN ? AND ? ");
                    params.add(convert(value.get(0), targetClass));
                    params.add(convert(value.get(1), targetClass));
                    break;

                default:
                    sql.append(" AND ").append(field).append(" = ? ");
                    params.add(convert(value.get(0), targetClass));
            }
        }

        return new SqlQuery(sql.toString(), params);
    }

    private Object convert(String value, Class<?> targetClass) {
        return conversionService.convert(value, targetClass);
    }

    private List<Object> convertList(List<String> values, Class<?> targetClass) {
        return Collections.singletonList(values.stream()
                .map(v -> conversionService.convert(v, targetClass))
                .toList());
    }

    private void appendPlaceholders(StringBuilder sql, int count) {
        for (int i = 0; i < count; i++) {
            sql.append("?");
            if (i < count - 1) sql.append(",");
        }
    }

    private Class<?> isAssignable(Class<?> clazz, String fieldName) {
        try {
            final Class<?> aClass = resolveNestedProperty(clazz, fieldName.split("\\.", -1));
            final boolean canConvert = conversionService.canConvert(String.class, aClass);
            if(!canConvert) {
                log.warn("Unable to find convertor for {}", fieldName);
                return null;
            }
            return aClass;
        } catch (Exception e) {
            log.warn("Unable to find assignable pproperty for "+fieldName, e);
        }
        return null;
    }

    private Class<?> getPropertyType(Class<?> clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName).getType();
        } catch (final NoSuchFieldException e) {
            if (!clazz.equals(Object.class)) {
                return getPropertyType(clazz.getSuperclass(), fieldName);
            }
            throw new IllegalStateException(e);
        }
    }

    private Class<?> resolveNestedProperty(Class<?> clazz, String[] fieldNames) {
        if (fieldNames.length > 1) {
            final String firstProperty = fieldNames[0];
            final Class<?> firstPropertyType = getPropertyType(clazz, firstProperty);
            return resolveNestedProperty(firstPropertyType, Arrays.copyOfRange(fieldNames, 1, fieldNames.length));
        }
        return getPropertyType(clazz, fieldNames[0]);
    }
}