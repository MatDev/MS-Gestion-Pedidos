package com.matdev.menuservice;

import jdk.jfr.Enabled;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Enabled
@SpringBootApplication
public class MenuSericeAplication {
    public static void main(String[] args) {
        SpringApplication.run(MenuSericeAplication.class, args);
    }
}
