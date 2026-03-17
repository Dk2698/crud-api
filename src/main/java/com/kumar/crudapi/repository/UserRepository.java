package com.kumar.crudapi.repository;


import com.kumar.crudapi.base.repo.BaseRepository;
import com.kumar.crudapi.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends BaseRepository<User, Long> {
    User findByEmailAndDeletedFalse(String email);
}