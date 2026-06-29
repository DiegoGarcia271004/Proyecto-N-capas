package org.example.warehouseinventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WarehouseInventoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(WarehouseInventoryApplication.class, args);
    }
}