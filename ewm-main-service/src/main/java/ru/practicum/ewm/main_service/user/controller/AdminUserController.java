package ru.practicum.ewm.main_service.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main_service.user.dto.NewUserRequest;
import ru.practicum.ewm.main_service.user.dto.UserDto;
import ru.practicum.ewm.main_service.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/users")
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers(@RequestParam(name = "ids", required = false) List<Long> ids,
                               @RequestParam(name = "from", defaultValue = "0") int from,
                               @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("MainService: Get Admin all users, ids={}, from={}, size={}", ids, from, size);
        return userService.findAllUsers(ids, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Valid @RequestBody NewUserRequest newUserRequest) {
        log.info("MainService: Admin create user, user={}", newUserRequest);
        return userService.saveUser(newUserRequest);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable("userId") long userId) {
        log.info("MainService: Admin delete user, userId={}", userId);
        userService.deleteUser(userId);
    }

}