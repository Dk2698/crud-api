package com.kumar.crudapi.base;

import com.kumar.crudapi.base.data.EntityDAO;
import com.kumar.crudapi.base.filter.CriteriaCondition;
import com.kumar.crudapi.base.filter.FilterPredicate;
import com.kumar.crudapi.base.filter.SimpleCriteria;
import com.kumar.crudapi.base.repo.BaseRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
public abstract class BaseCrudService<ID extends Serializable, D extends EntityDTO<ID>, E extends BaseEntity<ID>> {

    private static final String[] falseList = {"false"};

    private static final SimpleCriteria NOT_DELETED_FILTER = new SimpleCriteria("deleted", CriteriaCondition.EQUALS, Arrays.asList(falseList));

    protected BaseRepository<E, ID> repository;

    protected EntityMapper<D, E> mapper;

    @Autowired
    protected EntityDAO entityDAO;

    private String entityName;
    private Class<D> dtoClass;

    private Class<E> entityClass;

    public BaseCrudService(BaseRepository<E, ID> repository, EntityMapper<D, E> mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * Save an entity.
     *
     * @param dto for the entity to save.
     * @return the persisted entity.
     */
    public D save(D dto) {
        log.debug("Request to save {}  : {}", entityName, dto);
        E entity = mapper.toEntity(dto);
        log.debug("Transformed Entity is {}", entity);
        entity = repository.save(entity);
        return mapper.toDto(entity);
    }

    /**
     * Update an entity.
     *
     * @param dto for entity to save.
     * @return the persisted entity.
     */
    public D update(D dto) {
        log.debug("Request to update {} : {}", entityName, dto);
        E entity = mapper.toEntity(dto);
        entity = repository.save(entity);
        return mapper.toDto(entity);
    }

    /**
     * Partially update an entity.
     *
     * @param dto the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<D> partialUpdate(D dto) {
        log.debug("Request to partially update {} : {}", entityName, dto);

        return repository
                .findById(dto.getId())
                .map(existingCEntity -> {
                    mapper.partialUpdate(existingCEntity, dto);
                    return existingCEntity;
                })
                .map(repository::save)
                .map(mapper::toDto);
    }

    /**
     * Get filtered the Entities.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Page<D> findAll(FilterPredicate filter, Pageable pageable) {
        log.debug("Request to get all {}", entityName);
//        filter.add(NOT_DELETED_FILTER);
        final Page<E> entities = entityDAO.findAll(filter, pageable, entityClass);
        return entities.map(mapper::toDto);
    }


    /**
     * Get all the Entities.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Page<D> findAll(Pageable pageable) {
        log.debug("Request to get all {}", entityName);
        return findAll(new FilterPredicate(), pageable);
    }

    /**
     * Get one entity by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Optional<D> findOne(ID id) {
        log.debug("Request to get {} : {}", entityName, id);
        return repository.findByIdAndDeletedFalse(id)
                .map(mapper::toDto);
    }

    /**
     * Marks the entity with the given id as DELETED. Hard deletes are disabled by default
     *
     * @param id the id of the entity.
     */
    public void delete(ID id) {
        log.debug("Request to delete {} : {}", entityName, id);
        final Optional<E> entityOptional = repository.findById(id);
        if (entityOptional.isPresent()) {
            final E entity = entityOptional.get();
            entity.setDeleted(true);
            repository.save(entity);
        }
        //repository.deleteById(id);
    }

    @PostConstruct
    protected Class<E> getEntityClass() {
        if (entityClass == null) {
            ParameterizedType genericSuperclass = (ParameterizedType) this.getClass().getGenericSuperclass();
            entityClass = (Class<E>) genericSuperclass.getActualTypeArguments()[2];
            entityName = entityClass.getSimpleName();
        }
        return entityClass;
    }

    protected Class<D> getDTOClass() {
        if (dtoClass == null) {
            ParameterizedType genericSuperclass = (ParameterizedType) this.getClass()
                    .getGenericSuperclass();
            dtoClass = (Class<D>) genericSuperclass.getActualTypeArguments()[1];
        }
        return dtoClass;
    }
}