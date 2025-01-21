package zw.mohcc.org.prep.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import zw.mohcc.org.prep.dto.DemographicDTO;
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

    @Query("""
        SELECT new zw.mohcc.org.prep.dto.DemographicDTO(
            p.sex,
            p.populationType,
            CASE
                WHEN YEAR(CURRENT_DATE) - YEAR(p.dob) < 25 THEN 'Below 25'
                WHEN YEAR(CURRENT_DATE) - YEAR(p.dob) BETWEEN 25 AND 29 THEN '25-29'
                WHEN YEAR(CURRENT_DATE) - YEAR(p.dob) BETWEEN 30 AND 34 THEN '30-34'
                WHEN YEAR(CURRENT_DATE) - YEAR(p.dob) BETWEEN 35 AND 39 THEN '35-39'
                WHEN YEAR(CURRENT_DATE) - YEAR(p.dob) BETWEEN 40 AND 44 THEN '40-44'
                ELSE '45+'
            END,
            COUNT(p)
        )
        FROM Patient p
        GROUP BY p.sex, p.populationType,
        CASE
            WHEN YEAR(CURRENT_DATE) - YEAR(p.dob) < 25 THEN 'Below 25'
            WHEN YEAR(CURRENT_DATE) - YEAR(p.dob) BETWEEN 25 AND 29 THEN '25-29'
            WHEN YEAR(CURRENT_DATE) - YEAR(p.dob) BETWEEN 30 AND 34 THEN '30-34'
            WHEN YEAR(CURRENT_DATE) - YEAR(p.dob) BETWEEN 35 AND 39 THEN '35-39'
            WHEN YEAR(CURRENT_DATE) - YEAR(p.dob) BETWEEN 40 AND 44 THEN '40-44'
            ELSE '45+'
        END
        """)
    List<DemographicDTO> getPopulationDemographics();
}