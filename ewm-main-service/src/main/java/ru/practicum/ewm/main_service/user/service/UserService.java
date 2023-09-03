package ru.practicum.ewm.main_service.user.service;

import ru.practicum.ewm.main_service.user.dto.NewUserRequest;
import ru.practicum.ewm.main_service.user.dto.UserDto;
import ru.practicum.ewm.main_service.user.model.User;

import java.util.List;

public interface UserService {

    List<UserDto> findAllUsers(List<Long> ids, int from, int size);

    User findUserById(Long userId);

    UserDto saveUser(NewUserRequest newUserRequest);

    void deleteUser(long userId);

    void validateUserById(long userId);

}