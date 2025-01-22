package zw.mohcc.org.prep.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import zw.mohcc.org.prep.dto.InjectionTrendDTO;
import zw.mohcc.org.prep.dto.MissingFollowUpDTO;
import zw.mohcc.org.prep.dto.RetentionDTO;
import zw.mohcc.org.prep.entities.Patient;
import zw.mohcc.org.prep.entities.Visit;
import zw.mohcc.org.prep.enums.CurrentStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public interface VisitRepository extends JpaRepository<Visit, String> {
    List<Visit> findByPatient_PatientId(String patientPatientId);

    @Query("SELECT COUNT(v) FROM Visit v " +
            "WHERE v.injectionDate BETWEEN :startDate AND :endDate")
    int countVisitsBetweenDates(LocalDate startDate, LocalDate endDate);

    @Query("SELECT v.currentStatus as status, COUNT(v) as count " +
            "FROM Visit v " +
            "WHERE v.visitId IN (SELECT MAX(v2.visitId) FROM Visit v2 GROUP BY v2.patient) " +
            "GROUP BY v.currentStatus")
    List<Object[]> getStatusDistributionRaw();

    default Map<String, Integer> getStatusDistribution() {
        return getStatusDistributionRaw().stream()
                .collect(Collectors.toMap(
                        row -> ((CurrentStatus) row[0]).name(),
                        row -> ((Long) row[1]).intValue()
                ));
    }

    boolean existsByInjectionDateAndPatient(LocalDate injectionDate, Patient patient);

    @Query("""
        SELECT new zw.mohcc.org.prep.dto.RetentionDTO(
            v.typeOfInjection,
            v.currentStatus,
            v.discontinuationReason,
            COUNT(v)
        )
        FROM Visit v
        GROUP BY v.typeOfInjection, v.currentStatus, v.discontinuationReason
        """)
    List<RetentionDTO> getRetentionByInjectionType();

    @Query(value = """
        SELECT 
            YEAR(injection_date) as year,
            MONTH(injection_date) as month,
            COUNT(*) as total
        FROM visit v
        WHERE injection_date BETWEEN :startDate AND :endDate
        GROUP BY YEAR(injection_date), MONTH(injection_date)
        ORDER BY YEAR(injection_date), MONTH(injection_date)
        """, nativeQuery = true)
    List<Object[]> getInjectionTrends(LocalDate startDate, LocalDate endDate);

    @Query(value = """
        SELECT 
            p.patient_id,
            p.sex,
            p.population_type,
            MAX(v.injection_date) as last_injection,
            DATEDIFF(CURRENT_DATE, MAX(v.injection_date)) as days_since_last
        FROM patient p
        JOIN visit v ON p.patient_id = v.patient_id
        GROUP BY p.patient_id, p.sex, p.population_type
        HAVING DATEDIFF(CURRENT_DATE, MAX(v.injection_date)) > 90
        ORDER BY MAX(v.injection_date) ASC
        """, nativeQuery = true)
    List<Object[]> getMissingFollowUps();
}