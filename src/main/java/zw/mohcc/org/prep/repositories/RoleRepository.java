package zw.mohcc.org.prep.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zw.mohcc.org.prep.entities.Role;
import zw.mohcc.org.prep.enums.ERole;

import java.util.List;
import java.util.Optional;
import java.util.Set;


@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
    Set<Role> findByNameIn(List<String> name);
}
