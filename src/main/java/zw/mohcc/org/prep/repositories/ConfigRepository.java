package zw.mohcc.org.prep.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zw.mohcc.org.prep.entities.Config;

@Repository
public interface ConfigRepository extends JpaRepository<Config, Long> {
}
