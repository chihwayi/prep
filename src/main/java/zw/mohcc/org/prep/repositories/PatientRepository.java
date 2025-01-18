package zw.mohcc.org.prep.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import zw.mohcc.org.prep.entities.Patient;
import zw.mohcc.org.prep.enums.PopulationType;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public interface PatientRepository extends JpaRepository<Patient, String> {
    Optional<Patient> findByPrepNumber(String prepNumber);
    Page<Patient> findByPrepNumberContainingIgnoreCase(String filter, Pageable pageable);

    @Query("SELECT COUNT(DISTINCT p) FROM Patient p JOIN p.visits v " +
            "WHERE v.injectionDate >= :threeMonthsAgo " +
            "AND v.currentStatus = 'PX'")
    int countActivePatients(LocalDate threeMonthsAgo);

    @Query("SELECT p.populationType as type, COUNT(p) as count " +
            "FROM Patient p WHERE p.populationType IS NOT NULL " +
            "GROUP BY p.populationType")
    List<Object[]> getPopulationTypeDistributionRaw();

    default Map<String, Integer> getPopulationTypeDistribution() {
        return getPopulationTypeDistributionRaw().stream()
                .collect(Collectors.toMap(
                        row -> ((PopulationType) row[0]).name(),
                        row -> ((Long) row[1]).intValue()
                ));
    }
}