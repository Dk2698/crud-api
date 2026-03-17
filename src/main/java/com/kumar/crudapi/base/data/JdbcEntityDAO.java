package com.kumar.crudapi.base;

import com.kumar.crudapi.base.filter.FilterPredicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class JdbcEntityDAO implements EntityDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SqlQueryBuilder queryBuilder;

    @Override
    public <T> Page<T> findAll(FilterPredicate filter, Pageable pageable, Class<T> entityClass) {
        return null;
    }

    @Override
    public <T> List<T> findAll(FilterPredicate filter, Class<T> entityClass) {

        SqlQuery sqlQuery = queryBuilder.buildQuery(filter, entityClass);

        String table = entityClass.getSimpleName().toLowerCase();

        String sql = "SELECT * FROM " + table + sqlQuery.getWhereClause();

        return jdbcTemplate.query(
                sql,
                sqlQuery.getParams().toArray(),
                new BeanPropertyRowMapper<>(entityClass)
        );
    }

    @Override
    public <T> boolean exists(FilterPredicate filter, Class<T> entityClass) {
        return false;
    }

    @Override
    public <T> long count(FilterPredicate filter, Class<T> entityClass) {
        return 0;
    }


//    @Override
//    public <T> boolean exists(FilterPredicate filter, Class<T> entityClass) {
//
//        log.debug("Request to check existence of {}", entityClass.getSimpleName());
//
//        String query = queryBuilder.buildExistsQuery(filter, entityClass);
//
//        Integer count = jdbcTemplate.queryForObject(query, Integer.class);
//
//        return count != null && count > 0;
//    }
//
//    @Override
//    public <T> long count(FilterPredicate filter, Class<T> entityClass) {
//
//        log.debug("Request for count of {}", entityClass.getSimpleName());
//
//        String query = queryBuilder.buildCountQuery(filter, entityClass);
//
//        Long count = jdbcTemplate.queryForObject(query, Long.class);
//
//        return count != null ? count : 0;
//    }
}