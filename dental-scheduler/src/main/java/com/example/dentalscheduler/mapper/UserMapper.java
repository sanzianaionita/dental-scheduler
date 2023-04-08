package com.example.dentalscheduler.mapper;

import com.example.dentalscheduler.dto.UserDTO;
import com.example.dentalscheduler.model.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserDTO userDTO);

    UserDTO toDTO(User user);

    List<User> toEntity(List<UserDTO> userDTOs);

    List<UserDTO> toDTO(List<User> users);
}
