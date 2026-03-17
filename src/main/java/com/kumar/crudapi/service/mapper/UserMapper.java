package com.kumar.crudapi.service.mapper;


import com.kumar.crudapi.base.EntityMapper;
import com.kumar.crudapi.entity.User;
import com.kumar.crudapi.service.dto.UserDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper extends EntityMapper<UserDTO, User> {

}