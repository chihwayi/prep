package zw.mohcc.org.prep.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zw.mohcc.org.prep.entities.Facility;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, String> {
    Facility getFacilitiesBySiteCode(String siteCode);
}