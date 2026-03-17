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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
            final List<String> value = entry.getValue();

            // Skip empty or special params
            if (isSingleElementCollectionWithEmptyItem(value) || key.startsWith("_") || IGNORED_PARAMS.contains(key)) {
                continue;
            }

            // Existing filter logic
            if (key.contains(DELIMITER)) {
                String[] parts = key.split(DELIMITER);

                String rawField = parts[0];
                String field = CaseUtils.toCamelCase(rawField, false, '_');
                CriteriaCondition condition = CriteriaCondition.valueOfLabel(parts[1]);

                Object finalValue = value.size() == 1 ? value.get(0) : value;

                filterPredicate.add(new SimpleCriteria(field, condition, finalValue));

            } else {
                String field = CaseUtils.toCamelCase(key, false, '_');

                filterPredicate.add(value.size() == 1 ? new SimpleCriteria(field, CriteriaCondition.EQUALS, value.getFirst()) : new SimpleCriteria(field, CriteriaCondition.IN, value));
            }
        }
        return filterPredicate;
    }

}
