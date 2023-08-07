package ru.practicum.ewm.mainService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "ru.practicum.ewm")
public class MainService {
    public static void main(String[] args) {
        SpringApplication.run(MainService.class, args);
    }
}