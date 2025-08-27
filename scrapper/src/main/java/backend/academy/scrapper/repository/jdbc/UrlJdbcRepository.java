package backend.academy.scrapper.repository.jdbc;

import backend.academy.dto.validator.util.UrlType;
import backend.academy.scrapper.domain.UrlRepository;
import backend.academy.scrapper.domain.dto.UrlAddDto;
import backend.academy.scrapper.domain.dto.UrlInfoDto;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Repository;

@ConditionalOnProperty(prefix = "app", value = "data-access", havingValue = "jdbc")
@Repository
@RequiredArgsConstructor
public class UrlJdbcRepository implements UrlRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Long save(UrlAddDto url) {
        String sql =
                """
            INSERT INTO url (url_type, url, last_time_updated, meta)
            VALUES (?, ?, ?, ?)
            RETURNING id
            """;

        return jdbcTemplate.queryForObject(
                sql,
                Long.class,
                new SqlParameterValue(Types.OTHER, url.type().name()),
                url.url(),
                new SqlParameterValue(Types.TIMESTAMP, Timestamp.from(url.lastUpdateTime())),
                new SqlParameterValue(Types.OTHER, url.meta()));
    }

    @Override
    public Optional<Long> findByUrl(String url) {
        String sql = """
            SELECT u.id
            FROM url AS u
            WHERE u.url = ?
            """;
        return jdbcTemplate.query(
                sql, ps -> ps.setString(1, url), rs -> rs.next() ? Optional.of(rs.getLong("id")) : Optional.empty());
    }

    @Override
    public void updateLastTimeUpdated(String url, Instant lastTimeUpdated) {
        String sql = "UPDATE url AS u SET last_time_updated = ? WHERE u.url = ?";

        jdbcTemplate.update(sql, new SqlParameterValue(Types.OTHER, Timestamp.from(lastTimeUpdated)), url);
    }

    @Override
    public List<UrlInfoDto> getBatch(Long threshold, Integer limit) {
        String sql =
                """
            SELECT u.id, u.url, u.url_type, u.last_time_updated, u.meta
            FROM url AS u
            WHERE u.id > ?
            LIMIT ?
        """;

        return jdbcTemplate.query(sql, urlInfoDtoRowMapper(), threshold, limit);
    }

    private RowMapper<UrlInfoDto> urlInfoDtoRowMapper() {
        return (rs, unused) -> {
            Long id = rs.getLong("id");
            String url = rs.getString("url");
            UrlType type = UrlType.valueOf(rs.getString("url_type"));
            Instant lastUpdateTime = rs.getTimestamp("last_time_updated").toInstant();

            @SuppressWarnings("unchecked")
            Map<String, String> meta = (Map<String, String>) rs.getObject("meta");

            return new UrlInfoDto(id, url, type, lastUpdateTime, meta);
        };
    }
}
