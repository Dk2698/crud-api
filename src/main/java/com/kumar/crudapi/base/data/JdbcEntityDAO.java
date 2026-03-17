package com.kumar.crudapi.base.data;

import com.kumar.crudapi.base.filter.FilterPredicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
        log.debug("Request to get all {}", entityClass.getSimpleName());

        SqlQuery sqlQuery = queryBuilder.buildQuery(filter, pageable, entityClass);

        String table = entityClass.getSimpleName().toLowerCase() + "s";
        String sql = "SELECT * FROM " + table +
                sqlQuery.getWhereClause() +
                sqlQuery.getOrderByClause() +
                " LIMIT " + pageable.getPageSize() +
                " OFFSET " + pageable.getOffset();

        List<T> list = jdbcTemplate.query(
                sql,
                sqlQuery.getParams().toArray(),
                BeanPropertyRowMapper.newInstance(entityClass)
        );

        String countSql = queryBuilder.buildCountQuery(filter, entityClass);
        Integer total = jdbcTemplate.queryForObject(countSql, sqlQuery.getParams().toArray(), Integer.class);

        return new PageImpl<>(list, pageable, total != null ? total : 0);
    }

    @Override
    public <T> List<T> findAll(FilterPredicate filter, Class<T> entityClass) {
        SqlQuery sqlQuery = queryBuilder.buildQuery(filter, entityClass);
        String table = entityClass.getSimpleName().toLowerCase() + "s";
        String sql = "SELECT * FROM " + table + sqlQuery.getWhereClause() + sqlQuery.getOrderByClause();

        return jdbcTemplate.query(
                sql,
                sqlQuery.getParams().toArray(),
                BeanPropertyRowMapper.newInstance(entityClass)
        );
    }

    @Override
    public <T> boolean exists(FilterPredicate filter, Class<T> entityClass) {
        log.debug("Check existence of {}", entityClass.getSimpleName());
        SqlQuery sqlQuery = queryBuilder.buildQuery(filter, entityClass);
        String existsSql = queryBuilder.buildExistsQuery(filter, entityClass);

        Integer count = jdbcTemplate.queryForObject(
                existsSql,
                sqlQuery.getParams().toArray(),
                Integer.class
        );

        return count != null && count > 0;
    }

    @Override
    public <T> long count(FilterPredicate filter, Class<T> entityClass) {
        log.debug("Count for {}", entityClass.getSimpleName());
        SqlQuery sqlQuery = queryBuilder.buildQuery(filter, entityClass);
        String countSql = queryBuilder.buildCountQuery(filter, entityClass);

        Integer count = jdbcTemplate.queryForObject(
                countSql,
                sqlQuery.getParams().toArray(),
                Integer.class
        );

        return count != null ? count : 0;
    }
}