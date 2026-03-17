package com.kumar.crudapi.service;

import com.kumar.crudapi.base.BaseCrudService;
import com.kumar.crudapi.base.EntityMapper;
import com.kumar.crudapi.base.repo.BaseRepository;
import com.kumar.crudapi.entity.User;
import com.kumar.crudapi.service.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class UserService extends BaseCrudService<Long, UserDTO, User> {

    public UserService(BaseRepository<User, Long> repository, EntityMapper<UserDTO, User> mapper) {
        super(repository, mapper);
    }
}