package com.kumar.crudapi.base;


import com.kumar.crudapi.base.error.BadRequestException;
import com.kumar.crudapi.base.filter.FilterPredicate;
import com.kumar.crudapi.base.repo.BaseRepository;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Log4j2
public class BaseCrudController<ID extends Serializable, D extends EntityDTO<ID>, E extends BaseEntity<ID>> extends BaseController {

    protected BaseCrudService<ID, D, E> service;
    protected BaseRepository<E, ID> repository;
    private String entityName;

    public BaseCrudController(BaseCrudService<ID, D, E> service, BaseRepository<E, ID> repository) {
        this.service = service;
        this.repository = repository;
    }

    /**
     * {@code POST  /} : Create a new entity.
     *
     * @param dto the dto to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new dto, or with status {@code 400 (Bad Request)} if the entity has already an ID.
     */
    @PostMapping()
    public ResponseEntity<D> createEntity(@Valid @RequestBody D dto
                                          // ,  HttpServletRequest request
    ) {
        log.debug("REST request to save Entity : {}", dto);
        if (dto.getId() != null) {
            throw new BadRequestException("A new entity cannot already have an ID", entityName, "idexists");
        }
        D result = service.save(dto);
        return ResponseEntity
                //.created(extendRequestPath(request,result.getId().toString()))
                .created(URI.create("/"))
                .headers(createEntityCreationAlert(result.getId().toString()))
                .body(result);
    }

    /**
     * {@code PUT /:id} : Updates an existing entity.
     *
     * @param id  the id of the dto to save.
     * @param dto the dto to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dto,
     * or with status {@code 400 (Bad Request)} if the dto is not valid,
     * or with status {@code 500 (Internal Server Error)} if the dto couldn't be updated.
     */
    @PutMapping("/{id}")
    public ResponseEntity<D> updateEntity(
            @PathVariable(value = "id", required = false) final ID id,
            @Valid @RequestBody D dto) {
        log.debug("REST request to update Entity : {}, {}", id, dto);
        if (dto.getId() == null) {
            throw new BadRequestException("Invalid id", entityName, "idnull");
        }
        if (!Objects.equals(id, dto.getId())) {
            throw new BadRequestException("Invalid ID", entityName, "idinvalid");
        }

        if (!repository.existsById(id)) {
            throw new BadRequestException("Entity not found", entityName, "idnotfound");
        }

        D result = service.update(dto);
        return ResponseEntity
                .ok()
                .headers(createEntityUpdateAlert(dto.getId().toString()))
                .body(result);
    }

    /**
     * {@code PATCH  /:id} : Partial updates given fields of an existing entity, field will ignore if it is null
     *
     * @param id  the id of the dto to save.
     * @param dto the dto to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dto,
     * or with status {@code 400 (Bad Request)} if the dto is not valid,
     * or with status {@code 404 (Not Found)} if the dto is not found,
     * or with status {@code 500 (Internal Server Error)} if the dto couldn't be updated.
     */
    @PatchMapping(value = "/{id}", consumes = {"application/json", "application/merge-patch+json"})
    public ResponseEntity<D> partialUpdateEntity(
            @PathVariable(value = "id", required = false) final ID id,
            @NotNull @RequestBody D dto) {
        log.debug("REST request to partial update Entity partially : {}, {}", id, dto);
        if (dto.getId() == null) {
            throw new BadRequestException("Invalid id", entityName, "idnull");
        }
        if (!Objects.equals(id, dto.getId())) {
            throw new BadRequestException("Invalid ID", entityName, "idinvalid");
        }

        if (!repository.existsById(id)) {
            throw new BadRequestException("Entity not found", entityName, "idnotfound");
        }

        Optional<D> result = service.partialUpdate(dto);
        return wrapOrNotFound(result, createEntityUpdateAlert(dto.getId().toString()));
    }

    /**
     * {@code GET  /} : get all the courierGroups.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of courierGroups in body.
     */
    @GetMapping("")
    public ResponseEntity<List<D>> getAllEntities(FilterPredicate predicate, Pageable pageable, HttpServletRequest request) {
        log.debug("REST request to get a page of {}", entityName);
        Page<D> page = service.findAll(predicate, pageable);
        HttpHeaders headers = generatePaginationHttpHeaders(request, page);
        return ResponseEntity.ok()
                .headers(headers)
                .body(page.getContent());
    }

    /**
     * {@code GET  /:id} : get the "id" entity.
     *
     * @param id the id of the dto to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the dto, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<D> getEntity(@PathVariable ID id) {
        log.debug("REST request to get Entity : {}", id);
        Optional<D> dto = service.findOne(id);
        return wrapOrNotFound(dto);
    }

    /**
     * {@code DELETE  /:id} : delete the "id" entity.
     *
     * @param id the id of the dto to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEntity(@PathVariable ID id) {
        log.debug("REST request to delete Entity : {}", id);
        service.delete(id);
        return ResponseEntity.noContent()
                .headers(createEntityDeletionAlert(id.toString()))
                .build();
    }


    /**
     * <p>createEntityCreationAlert.</p>
     *
     * @param param a {@link String} object.
     * @return a {@link HttpHeaders} object.
     */
    protected HttpHeaders createEntityCreationAlert(String param) {
        String message = String.format("%s.created", getEntityName());
        String defaultMessage = String.format("%s is created with identifier %s", getEntityName(), param);
        return createHeaderAlert(message, param, defaultMessage);
    }

    /**
     * <p>createEntityUpdateAlert.</p>
     *
     * @param param a {@link String} object.
     * @return a {@link HttpHeaders} object.
     */
    protected HttpHeaders createEntityUpdateAlert(String param) {
        String message = String.format("%s.updated", getEntityName());
        String defaultMessage = String.format("%s is updated with identifier %s", getEntityName(), param);
        return createHeaderAlert(message, param, defaultMessage);
    }

    /**
     * <p>createEntityDeletionAlert.</p>
     *
     * @param param a {@link String} object.
     * @return a {@link HttpHeaders} object.
     */
    protected HttpHeaders createEntityDeletionAlert(String param) {
        String message = String.format("%s.deleted", getEntityName());
        String defaultMessage = String.format("%s is deleted with identifier %s", getEntityName(), param);
        return createHeaderAlert(message, param, defaultMessage);
    }

    /**
     * <p>createFailureAlert.</p>
     *
     * @param errorKey       a {@link String} object.
     * @param defaultMessage a {@link String} object.
     * @return a {@link HttpHeaders} object.
     */
    protected HttpHeaders createEntityFailureAlert(String errorKey, String defaultMessage) {

        defaultMessage = defaultMessage !=
                null ? defaultMessage : String.format("An error occurred while processing %s", getEntityName());
        return createFailureHeaderAlert(errorKey, getEntityName(), defaultMessage);
    }

    @PostConstruct
    protected String getEntityName() {
        if (entityName == null) {
            ParameterizedType genericSuperclass = (ParameterizedType) this.getClass()
                    .getGenericSuperclass();
            Class<E> claaz = (Class<E>) genericSuperclass.getActualTypeArguments()[2];
            entityName = claaz.getSimpleName();
        }
        return entityName;
    }
}