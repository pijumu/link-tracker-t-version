package backend.academy.scrapper.repository.jpa.entity;

import backend.academy.dto.validator.util.UrlType;
import io.hypersistence.utils.hibernate.type.basic.PostgreSQLHStoreType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "url")
@Getter
@Setter
@NoArgsConstructor
public class UrlEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "url_type", nullable = false)
    private UrlType type;

    @Column(name = "url", nullable = false, unique = true)
    private String url;

    @Column(name = "last_time_updated", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant lastTimeUpdated;

    @Type(PostgreSQLHStoreType.class)
    @Column(columnDefinition = "HSTORE")
    private Map<String, String> meta = new HashMap<>();
}
