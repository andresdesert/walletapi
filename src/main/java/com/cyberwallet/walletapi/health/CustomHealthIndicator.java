package com.cyberwallet.walletapi.health;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class CustomHealthIndicator implements HealthIndicator {

    private static final String EXTERNAL_SERVICE = "externalService";

    private final JdbcTemplate jdbcTemplate;

    @Value("${spring.datasource.url:jdbc:postgresql://db:5432/cyberwallet}")
    private String dbUrl;

    public CustomHealthIndicator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Health health() {
        // Check espacio libre en disco
        File root = new File("/");
        long freeSpace = root.getFreeSpace();

        if (freeSpace < 100 * 1024 * 1024) { // <100MB
            return Health.down()
                    .withDetail("disk", "Espacio en disco crítico")
                    .withDetail("libre", freeSpace + " bytes")
                    .build();
        }

        // Check conexión a la DB (si JdbcTemplate está bien configurado)
        try {
            jdbcTemplate.execute("SELECT 1");
        } catch (Exception e) {
            return Health.down()
                    .withDetail("db", "No se pudo conectar a la base de datos")
                    .withException(e)
                    .build();
        }

        // Check ficticio a un servicio externo
        boolean servicioExternoDisponible = true; // simulado

        if (!servicioExternoDisponible) {
            return Health.down()
                    .withDetail(EXTERNAL_SERVICE, "No disponible")
                    .build();
        }

        return Health.up()
                .withDetail("libre", freeSpace + " bytes")
                .withDetail("db", "OK")
                .withDetail(EXTERNAL_SERVICE, "Disponible")
                .build();
    }
}
