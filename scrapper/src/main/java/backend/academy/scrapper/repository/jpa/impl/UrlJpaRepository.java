package backend.academy.scrapper.repository.jpa.impl;

import backend.academy.scrapper.repository.jpa.entity.UrlEntity;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@ConditionalOnProperty(prefix = "app", value = "data-access", havingValue = "jpa")
@Repository
public interface UrlJpaRepository extends JpaRepository<UrlEntity, Long> {
    Optional<UrlEntity> findByUrl(String url);

    Page<UrlEntity> findByIdGreaterThan(Long id, Pageable pageable);
}
