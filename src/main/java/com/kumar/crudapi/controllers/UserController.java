package com.kumar.crudapi.controllers;

import com.kumar.crudapi.base.BaseCrudController;
import com.kumar.crudapi.base.BaseCrudService;
import com.kumar.crudapi.base.repo.BaseRepository;
import com.kumar.crudapi.entity.User;
import com.kumar.crudapi.service.dto.UserDTO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController extends BaseCrudController<Long, UserDTO, User> {

    public UserController(BaseCrudService<Long, UserDTO, User> service, BaseRepository<User, Long> repository) {
        super(service, repository);
    }
}