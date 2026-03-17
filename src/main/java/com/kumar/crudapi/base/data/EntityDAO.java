package com.kumar.crudapi.base;

import com.kumar.crudapi.base.filter.FilterPredicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EntityDAO {

    <T> Page<T> findAll(FilterPredicate filter, Pageable pageable, Class<T> entityClass);

    <T> List<T> findAll(FilterPredicate filter, Class<T> entityClass);

    <T> boolean exists(FilterPredicate filter, Class<T> entityClass);
    <T> long count(FilterPredicate filter, Class<T> entityClass);

}