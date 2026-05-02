package br.com.clinicah.config;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Executa flyway.repair() antes de migrate() para limpar entradas FAILED
 * na tabela flyway_schema_history (ex: migrations que falharam em deploys
 * anteriores). Sem isso, o Flyway recusa subir até que o repair seja feito
 * manualmente.
 */
@Configuration
public class FlywayRepairConfig {

    @Bean
    public FlywayMigrationStrategy repairThenMigrate() {
        return (Flyway flyway) -> {
            flyway.repair();
            flyway.migrate();
        };
    }
}
