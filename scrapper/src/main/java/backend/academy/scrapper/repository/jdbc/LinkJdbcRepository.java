package backend.academy.scrapper.repository.jdbc;

import backend.academy.scrapper.domain.LinkRepository;
import backend.academy.scrapper.domain.dto.LinkAddDto;
import backend.academy.scrapper.domain.dto.LinkDto;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@ConditionalOnProperty(prefix = "app", value = "data-access", havingValue = "jdbc")
@Repository
@RequiredArgsConstructor
public class LinkJdbcRepository implements LinkRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public Long save(LinkAddDto link, Long chatId, Long urlId) {
        String sql =
                """
            INSERT INTO link (chat_id, url_id, tags, filters)
            VALUES (?, ?, ?, ?)
            RETURNING id
            """;

        return jdbcTemplate
                .query(
                        sql,
                        ps -> {
                            ps.setLong(1, chatId);
                            ps.setLong(2, urlId);
                            ps.setArray(
                                    3,
                                    ps.getConnection()
                                            .createArrayOf("text", link.tags().toArray()));
                            ps.setArray(
                                    4,
                                    ps.getConnection()
                                            .createArrayOf(
                                                    "text", link.filters().toArray()));
                        },
                        (rs, rowNum) -> rs.getLong("id"))
                .getFirst();
    }

    @Override
    public LinkDto findByChatIdAndUrl(Long chatId, String url) {
        String sql =
                """
            SELECT l.id, u.url, l.filters, l.tags
            FROM link AS l
            JOIN url AS u on l.url_id = u.id
            WHERE l.chat_id = ? AND u.url = ?
            """;
        return jdbcTemplate.queryForObject(sql, linkMapper(), chatId, url);
    }

    @Override
    public void deleteById(Long linkId) {
        String sql = """
            DELETE FROM link AS l
            WHERE l.id = ?
            """;
        jdbcTemplate.update(sql, linkId);
    }

    @Override
    public List<LinkDto> findAllByChatId(Long chatId) {
        String sql =
                """
            SELECT l.id, u.url, l.filters, l.tags
            FROM link AS l
            JOIN url AS u ON l.url_id = u.id
            WHERE l.chat_id = ?
        """;

        return jdbcTemplate.query(sql, linkMapper(), chatId);
    }

    @Override
    public List<LinkDto> findAllByChatIdWithTags(Long chatId, List<String> tags) {
        String sql =
                """
            SELECT l.id, u.url, l.filters, l.tags
            FROM link AS l
            JOIN url AS u ON l.url_id = u.id
            WHERE l.chat_id = ? AND l.tags @> ?
        """;

        return jdbcTemplate.query(
                sql,
                ps -> {
                    ps.setLong(1, chatId);
                    ps.setArray(2, ps.getConnection().createArrayOf("text", tags.toArray()));
                },
                linkMapper());
    }

    @Override
    public Optional<LinkDto> updateTagsByLinkId(Long linkId, List<String> tags) {
        String sql =
                """
            UPDATE link AS l
            SET l.tags = ?
            FROM url AS u
            WHERE l.id = ? AND l.url_id = u.id
            RETURNING l.id, u.url, l.filters, l.tags
            """;

        List<LinkDto> result = jdbcTemplate.query(
                sql,
                ps -> {
                    ps.setArray(1, ps.getConnection().createArrayOf("text", tags.toArray()));
                    ps.setLong(2, linkId);
                },
                linkMapper());
        return result.stream().findFirst();
    }

    private RowMapper<LinkDto> linkMapper() {
        return (rs, rowNum) -> {
            Long id = rs.getLong("id");
            String url = rs.getString("url");
            List<String> filters = List.of((String[]) rs.getArray("filters").getArray());
            List<String> tags = List.of((String[]) rs.getArray("tags").getArray());
            return new LinkDto(id, url, filters, tags);
        };
    }
}
