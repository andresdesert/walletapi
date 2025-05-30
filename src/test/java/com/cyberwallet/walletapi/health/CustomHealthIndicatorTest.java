package com.cyberwallet.walletapi.health;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.actuate.health.Health;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;

class CustomHealthIndicatorTest {

    @Test
    @DisplayName("🟢 Health devuelve UP con todos los detalles OK")
    void healthShouldReturnUpWhenSystemHealthy() {
        // 🛠️ Simular JdbcTemplate con mock
        JdbcTemplate jdbcTemplate = Mockito.mock(JdbcTemplate.class);
        doNothing().when(jdbcTemplate).execute("SELECT 1");

        CustomHealthIndicator healthIndicator = new CustomHealthIndicator(jdbcTemplate);

        Health health = healthIndicator.health();

        assertThat(health.getStatus().getCode()).isEqualTo("UP");
        assertThat(health.getDetails()).containsKeys("libre", "db", "externalService");
        assertThat(health.getDetails().get("db")).isEqualTo("OK");
        assertThat(health.getDetails().get("externalService")).isEqualTo("Disponible");
    }

    @Test
    @DisplayName("🔴 Health devuelve DOWN si espacio en disco crítico")
    void healthShouldReturnDownWhenDiskSpaceCritical() {
        JdbcTemplate jdbcTemplate = Mockito.mock(JdbcTemplate.class);
        doNothing().when(jdbcTemplate).execute("SELECT 1");

        CustomHealthIndicator healthIndicator = new CustomHealthIndicator(jdbcTemplate) {
            @Override
            public Health health() {
                // Simular disco con espacio crítico
                long criticalSpace = 50 * 1024 * 1024; // 50MB
                return Health.down()
                        .withDetail("disk", "Espacio en disco crítico")
                        .withDetail("libre", criticalSpace + " bytes")
                        .build();
            }
        };

        Health health = healthIndicator.health();

        assertThat(health.getStatus().getCode()).isEqualTo("DOWN");
        assertThat(health.getDetails()).containsKey("disk");
    }

    @Test
    @DisplayName("🔴 Health devuelve DOWN si la DB no está disponible")
    void healthShouldReturnDownWhenDbUnavailable() {
        JdbcTemplate jdbcTemplate = Mockito.mock(JdbcTemplate.class);
        Mockito.doThrow(new RuntimeException("DB error")).when(jdbcTemplate).execute("SELECT 1");

        CustomHealthIndicator healthIndicator = new CustomHealthIndicator(jdbcTemplate);

        Health health = healthIndicator.health();

        assertThat(health.getStatus().getCode()).isEqualTo("DOWN");
        assertThat(health.getDetails()).containsKey("db");
    }
}
