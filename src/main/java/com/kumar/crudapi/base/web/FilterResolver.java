package com.kumar.crudapi.base.data;

//import com.logistiex.common.data.filter.CriteriaCondition;
//import com.logistiex.common.data.filter.FilterPredicate;
//import com.logistiex.common.data.filter.SimpleCriteria;
import com.kumar.crudapi.base.filter.CriteriaCondition;
import com.kumar.crudapi.base.filter.FilterPredicate;
import com.kumar.crudapi.base.filter.SimpleCriteria;
import jakarta.validation.constraints.NotNull;
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

    /**
     * Returns whether the given collection has exactly one element that is empty (i.e. doesn't contain text). This is
     * basically an indicator that a request parameter has been submitted but no value for it.
     *
     * @param source must not be {@literal null}.
     * @return true if the value collection is empty
     */
    private static boolean isSingleElementCollectionWithEmptyItem(List<?> source) {
        return source.size() == 1 && ObjectUtils.isEmpty(source.get(0));
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameter().getType() == FilterPredicate.class;
    }

    //    FilterPredicate getPredicate(MethodParameter parameter, MultiValueMap<String, String> queryParameters) {
    //
    //        MergedAnnotations annotations = MergedAnnotations.from(parameter.getParameter());
    //        MergedAnnotation<QuerydslPredicate> predicateAnnotation = annotations.get(QuerydslPredicate.class);
    //
    //        TypeInformation<?> domainType = extractTypeInfo(parameter, predicateAnnotation).getRequiredActualType();
    //
    //        Optional<Class<? extends QuerydslBinderCustomizer<?>>> bindingsAnnotation = predicateAnnotation.getValue("bindings") //
    //                .map(CastUtils::cast);
    //
    //        QuerydslBindings bindings = bindingsAnnotation //
    //                .map(it -> bindingsFactory.createBindingsFor(domainType, it)) //
    //                .orElseGet(() -> bindingsFactory.createBindingsFor(domainType));
    //
    //        return predicateBuilder.getPredicate(domainType, queryParameters, bindings);
    //    }

    @Override
    public Object resolveArgument(@NotNull MethodParameter parameter, ModelAndViewContainer mavContainer, @NotNull NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        final MultiValueMap<String, String> parameters = getQueryParameters(webRequest);
        final FilterPredicate filterPredicate = new FilterPredicate();
        for (var entry : parameters.entrySet()) {
            String key = entry.getKey().trim();
            final List<String> value = entry.getValue();
            if (isSingleElementCollectionWithEmptyItem(value)) {
                log.debug("{} ignored due to empty value", key);
                continue;
            }

            if (key.equals("_s") && !value.isEmpty()) {
//                filterPredicate.setWildSearchText(value.get(0));
                continue;
            } else if (key.startsWith("_")) {
                // ignore special params as we don't expect entity fields starting with "_"
                log.debug("ignore special params as we don't expect entity fields starting with _ {} ", key);
                continue;
            }
            log.debug("Processing input {} ", key);
            if (key.contains(DELIMITER)) {
                final String[] strings = key.split(DELIMITER);
                String fieldName = CaseUtils.toCamelCase(strings[0], false, '_');
                if (strings.length > 1) {
                    final CriteriaCondition condition = CriteriaCondition.valueOfLabel(strings[1]);
                    if (condition != null) {
                        switch (condition) {
                            case BETWEEN, NOT_BETWEEN:
                                if (value.size() == 2) {
                                    filterPredicate.add(new SimpleCriteria(fieldName, condition, value));
                                } else {
                                    throw new IllegalArgumentException(
                                            "Request parameter '" + strings[0] + " has " + value.size() +
                                                    " values, required 2");
                                }
                                break;
                            default:
                                filterPredicate.add(new SimpleCriteria(fieldName, condition, value));
                        }

                    } else {
                        log.debug("Skipping as we did not find matching condition");
                        // special param or it may be unsupported operation -
                        //Assuming it might be a special param, we will ignore
                    }
                }
            } else {
                String fieldName = CaseUtils.toCamelCase(key, false, '_');
                if (value.size() == 1) {
                    filterPredicate.add(new SimpleCriteria(fieldName, CriteriaCondition.EQUALS, value));
                } else {
                    filterPredicate.add(new SimpleCriteria(fieldName, CriteriaCondition.IN, value));
                }
            }
        }
        return filterPredicate;
    }

    //    @Nullable
    //    private Object getValue(TypeDescriptor targetType, Object value) {
    //
    //        if (ClassUtils.isAssignableValue(targetType.getType(), value)) {
    //            return value;
    //        }
    //
    //        if (conversionService.canConvert(value.getClass(), targetType.getType())) {
    //            return conversionService.convert(value, TypeDescriptor.forObject(value), targetType);
    //        }
    //
    //        return value;
    //    }

}
