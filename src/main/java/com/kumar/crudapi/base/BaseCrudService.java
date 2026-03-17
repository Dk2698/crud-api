package com.kumar.crudapi.base;

public abstract class GenericService<E extends BaseEntity<ID>, ID, RQ, RS> {
    protected final JpaRepository<E, ID> repository;
    protected final GenericMapper<E, RQ, RS> mapper;

    public GenericService(JpaRepository<E, ID> repository, GenericMapper<E, RQ, RS> mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public RS save(RQ request) {
        E entity = mapper.toEntity(request);
        return mapper.toResponse(repository.save(entity));
    }

    public List<RS> findAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public Optional<RS> findById(ID id) {
        return repository.findById(id)
                .map(mapper::toResponse);
    }

    public void deleteById(ID id) {
        repository.deleteById(id);
    }

    public RS update(ID id, RQ request) {
        E entity = repository.findById(id).orElseThrow();
        // update logic
        return mapper.toResponse(repository.save(entity));
    }
}