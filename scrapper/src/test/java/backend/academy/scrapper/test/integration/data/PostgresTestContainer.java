package backend.academy.scrapper.test.integration.data;

import java.io.File;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.DirectoryResourceAccessor;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class PostgresTestContainer {

    @Container
    protected static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.datasource.driver-class-name", POSTGRES::getDriverClassName);
        registry.add("liquibase.datasource.url", POSTGRES::getJdbcUrl);
    }

    @BeforeAll
    static void applyMigrations() {
        POSTGRES.start();

        try (Connection conn =
                DriverManager.getConnection(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword())) {
            Liquibase liquibase = new Liquibase(
                    "master.xml",
                    new DirectoryResourceAccessor(
                            new File(Paths.get("../migrations").toAbsolutePath().toString())),
                    DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(conn)));
            liquibase.update("");
        } catch (Exception e) {
            throw new RuntimeException("Failed to apply Liquibase migrations", e);
        }
    }
}
