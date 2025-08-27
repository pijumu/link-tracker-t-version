package backend.academy.scrapper.repository.jdbc;

import backend.academy.scrapper.domain.ChatRepository;
import backend.academy.scrapper.domain.dto.ChatDto;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@ConditionalOnProperty(prefix = "app", value = "data-access", havingValue = "jdbc")
@Repository
@RequiredArgsConstructor
public class ChatJdbcRepository implements ChatRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void save(ChatDto chatDto) {
        String sql = "INSERT INTO chat (id) VALUES (?)";
        jdbcTemplate.update(sql, chatDto.id());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM chat WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT EXISTS(SELECT 1 FROM chat WHERE id = ?)";
        Boolean exists = jdbcTemplate.queryForObject(sql, Boolean.class, id);
        return Boolean.TRUE.equals(exists);
    }
}
