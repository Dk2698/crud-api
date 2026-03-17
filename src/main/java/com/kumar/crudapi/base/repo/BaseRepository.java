package com.kumar.crudapi.base.repo;

import com.kumar.crudapi.base.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity<ID>, ID extends Serializable> extends JpaRepository<T, ID> {

    default void softDelete(T entity) {
        if (entity instanceof BaseEntity<?> base) {
            base.setDeleted(true);
            save(entity);
        }
    }

    default List<T> findAllActive() {
        return findAll().stream().filter(e -> !e.getDeleted()).toList();
    }

    //        default void softDelete(T entity) {
//            entity.softDelete();
//            save(entity);
//        }
    Optional<T> findByIdAndDeletedFalse(ID id);
}