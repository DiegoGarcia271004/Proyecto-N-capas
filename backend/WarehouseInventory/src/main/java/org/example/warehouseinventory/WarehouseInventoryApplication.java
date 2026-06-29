package org.example.warehouseinventory;

import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
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

    @Bean
    public CommandLineRunner seedData(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
        return args -> {
            // Seed users
            jdbcTemplate.execute("INSERT INTO users (id, username, password, role, active) " +
                    "VALUES (gen_random_uuid(), 'admin_wms', '" + passwordEncoder.encode("password123") + "', 'ADMIN', true) " +
                    "ON CONFLICT (username) DO NOTHING");
            jdbcTemplate.execute("INSERT INTO users (id, username, password, role, active) " +
                    "VALUES (gen_random_uuid(), 'jefe_madrid', '" + passwordEncoder.encode("password123") + "', 'WAREHOUSE_MANAGER', true) " +
                    "ON CONFLICT (username) DO NOTHING");
            jdbcTemplate.execute("INSERT INTO users (id, username, password, role, active) " +
                    "VALUES (gen_random_uuid(), 'operario_juan', '" + passwordEncoder.encode("password123") + "', 'OPERATOR', true) " +
                    "ON CONFLICT (username) DO NOTHING");

            // Seed warehouses
            jdbcTemplate.execute("INSERT INTO warehouse (id, created_at, updated_at, name, address, active) " +
                    "VALUES ('11111111-1111-1111-1111-111111111111', NOW(), NOW(), 'Almacén Central Madrid', 'Madrid, España', true) " +
                    "ON CONFLICT (id) DO NOTHING");
            jdbcTemplate.execute("INSERT INTO warehouse (id, created_at, updated_at, name, address, active) " +
                    "VALUES ('22222222-2222-2222-2222-222222222222', NOW(), NOW(), 'Almacén Satélite Barcelona', 'Barcelona, España', true) " +
                    "ON CONFLICT (id) DO NOTHING");
            jdbcTemplate.execute("INSERT INTO warehouse (id, created_at, updated_at, name, address, active) " +
                    "VALUES ('33333333-3333-3333-3333-333333333333', NOW(), NOW(), 'Almacén Pulmón Valencia', 'Valencia, España', true) " +
                    "ON CONFLICT (id) DO NOTHING");

            // Seed products
            seedProductSql(jdbcTemplate, "11111111-1111-1111-2222-111111111111", "SKU-99812", "Tornillo Acero Inoxidable 1/2", 1500, 2000, "TOOLS");
            seedProductSql(jdbcTemplate, "11111111-1111-1111-2222-222222222222", "SKU-88291", "Cable Eléctrico Cobre 10m", 100, 250, "ELECTRONICS");
            seedProductSql(jdbcTemplate, "11111111-1111-1111-2222-333333333333", "SKU-77382", "Interruptor Térmico 16A", 200, 400, "ELECTRONICS");
            seedProductSql(jdbcTemplate, "11111111-1111-1111-2222-444444444444", "SKU-66291", "Pintura Epóxica Industrial Gris 5G", 15, 30, "CHEMICALS");
            seedProductSql(jdbcTemplate, "11111111-1111-1111-2222-555555555555", "SKU-55481", "Cinta Aislante Negra 20m", 500, 800, "ELECTRONICS");
            seedProductSql(jdbcTemplate, "11111111-1111-1111-2222-666666666666", "SKU-44392", "Fusible Cerámico Rápido 10A", 300, 1000, "ELECTRONICS");
            seedProductSql(jdbcTemplate, "11111111-1111-1111-2222-777777777777", "SKU-33291", "Tubo Termocontraíble 1m", 300, 500, "OTHER");
            seedProductSql(jdbcTemplate, "11111111-1111-1111-2222-888888888888", "SKU-22198", "Aceite Lubricante Multiuso 1L", 50, 100, "CHEMICALS");

            System.out.println("Seeded database with users, warehouses, and products successfully using SQL.");
        };
    }

    private void seedProductSql(JdbcTemplate jdbcTemplate, String id, String sku, String name,
                                int minStock, int reorder, String category) {
        jdbcTemplate.execute("INSERT INTO product (id, created_at, updated_at, sku, product_name, min_stock_level, reorder_point, product_category, active) " +
                "VALUES ('" + id + "', NOW(), NOW(), '" + sku + "', '" + name + "', " + minStock + ", " + reorder + ", '" + category + "', true) " +
                "ON CONFLICT (id) DO NOTHING");
    }
}