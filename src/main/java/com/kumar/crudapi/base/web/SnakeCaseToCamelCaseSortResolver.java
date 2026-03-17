package com.kumar.crudapi.base.web;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.CaseUtils;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.List;

@Slf4j
public class SnakeCaseToCamelCaseSortResolver extends SortHandlerMethodArgumentResolver {

    @Override
    public Sort resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        final Sort sort = super.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
        log.debug("Sort parsed by default is {} ", sort.toString());
        final List<Sort.Order> orderList = sort.stream()
                .map(order -> new Sort.Order(order.getDirection(), CaseUtils.toCamelCase(order.getProperty(), false, '_'), order.getNullHandling()))
                .toList();
        log.debug("processed orders are {} ", orderList);
        return Sort.by(orderList);
    }
}
