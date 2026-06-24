package org.example.warehouseinventory;

import org.example.warehouseinventory.auth.domain.entity.Role;
import org.example.warehouseinventory.auth.domain.entity.User;
import org.example.warehouseinventory.auth.infraestructure.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableScheduling
public class WarehouseInventoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(WarehouseInventoryApplication.class, args);
    }

}