package ru.practicum.shareit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class ShareItApp {
    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(ShareItApp.class, args);
    }
}