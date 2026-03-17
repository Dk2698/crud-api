package com.kumar.crudapi.base.web;

import com.kumar.crudapi.base.filter.CriteriaCondition;
import com.kumar.crudapi.base.filter.FilterPredicate;
import com.kumar.crudapi.base.filter.SimpleCriteria;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.text.CaseUtils;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.*;

@Log4j2
public class FilterResolver implements HandlerMethodArgumentResolver {

    public static final String DELIMITER = ":";
    private ConversionService conversionService;

    private static MultiValueMap<String, String> getQueryParameters(NativeWebRequest webRequest) {

        Map<String, String[]> parameterMap = webRequest.getParameterMap();
        MultiValueMap<String, String> queryParameters = new LinkedMultiValueMap<>(parameterMap.size());

        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            queryParameters.put(entry.getKey(), Arrays.asList(entry.getValue()));
        }

        return queryParameters;
    }

    private static boolean isSingleElementCollectionWithEmptyItem(List<?> source) {
        return source.size() == 1 && ObjectUtils.isEmpty(source.get(0));
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameter().getType() == FilterPredicate.class;
    }

    private static final Set<String> IGNORED_PARAMS = Set.of("page", "size", "sort");

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        final MultiValueMap<String, String> parameters = getQueryParameters(webRequest);
        final FilterPredicate filterPredicate = new FilterPredicate();

        for (var entry : parameters.entrySet()) {

            String key = entry.getKey().trim();
            List<String> value = new ArrayList<>(entry.getValue());

            if (key.startsWith("_") || IGNORED_PARAMS.contains(key)) {
                continue;
            }

            String field;
            CriteriaCondition condition;

            // 🔹 Parse field + operator FIRST
            if (key.contains(DELIMITER)) {
                String[] parts = key.split(DELIMITER);

                field = CaseUtils.toCamelCase(parts[0], false, '_');
                condition = CriteriaCondition.valueOfLabel(parts[1]);

            } else {
                field = CaseUtils.toCamelCase(key, false, '_');
                condition = (value.size() > 1) ? CriteriaCondition.IN : CriteriaCondition.EQ;
            }

            // 🔥 Allow empty values ONLY for special operators
            boolean isNoValueOperator = switch (condition) {
                case IS_NULL, NOT_NULL, TRUE, FALSE -> true;
                default -> false;
            };

            if (!isNoValueOperator && isSingleElementCollectionWithEmptyItem(value)) {
                continue;
            }

            Object finalValue = normalizeValue(condition, value);

            filterPredicate.add(new SimpleCriteria(field, condition, finalValue));
        }
        return filterPredicate;
    }

    private Object normalizeValue(CriteriaCondition condition, List<String> values) {

        // Handle comma-separated values
        if (values.size() == 1 && values.get(0) != null && values.get(0).contains(",")) {
            values = Arrays.stream(values.get(0).split(","))
                    .map(String::trim)
                    .toList();
        }

        return switch (condition) {

            case IN, NOT_IN -> values;

            case BETWEEN, NOT_BETWEEN -> {
                if (values.size() < 2) {
                    throw new IllegalArgumentException("BETWEEN requires 2 values");
                }
                yield values;
            }

            case IS_NULL, NOT_NULL, TRUE, FALSE -> null;

            default -> values.get(0);
        };
    }
}
