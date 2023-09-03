package ru.practicum.ewm.main_service.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main_service.exception.error.ObjectNotFoundException;
import ru.practicum.ewm.main_service.exception.error.ObjectAlreadyExistException;
import ru.practicum.ewm.main_service.user.dto.NewUserRequest;
import ru.practicum.ewm.main_service.user.dto.UserDto;
import ru.practicum.ewm.main_service.user.mapper.UserMapper;
import ru.practicum.ewm.main_service.user.model.User;
import ru.practicum.ewm.main_service.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserDto> findAllUsers(List<Long> ids, int from, int size) {
        List<User> users;
        Pageable page = PageRequest.of(from / size, size);
        if (ids == null) {
            users = userRepository.findAll(page).toList();
            log.info("Got a list of users in the repository, from={}, size={}", from, size);
        } else {
            users = userRepository.findAllByIdIn(ids, page);
            log.info("Got a list of users in the repository, requested by users: {}, from={}, size={}",
                    ids.size(), from, size);
        }
        return users.stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("User not found: id=%d", userId)));
    }

    @Transactional
    @Override
    public UserDto saveUser(NewUserRequest newUserRequest) {
        try {
            log.info("Save user in the repository, user={}", newUserRequest);
            return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(newUserRequest)));
        } catch (DataIntegrityViolationException e) {
            log.error("User with email is already registered, email={}", newUserRequest.getEmail());
            throw new ObjectAlreadyExistException("User with email " +
                    newUserRequest.getEmail() + " is already registered.");
        }
    }

    @Transactional
    @Override
    public void deleteUser(long userId) {
        validateUserById(userId);
        userRepository.deleteById(userId);
        log.info("User deleted by userId={}", userId);
    }

    @Override
    public void validateUserById(long userId) {
        if (!userRepository.existsById(userId)) {
            log.error("User not found, id={}", userId);
            throw new ObjectNotFoundException(String.format("User not found: id=%d", userId));
        }
    }

}