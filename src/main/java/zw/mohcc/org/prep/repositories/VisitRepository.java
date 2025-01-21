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
    SELECT date_trunc('month', v.injection_date) AS month, COUNT(v.id) AS total
    FROM visit v
    WHERE v.injection_date BETWEEN :startDate AND :endDate
    GROUP BY date_trunc('month', v.injection_date)
    ORDER BY date_trunc('month', v.injection_date)
    """, nativeQuery = true)
    List<InjectionTrendDTO> getInjectionTrends(LocalDate startDate, LocalDate endDate);

    @Query("""
        SELECT new zw.mohcc.org.prep.dto.MissingFollowUpDTO(
            p.patientId,
            p.sex,
            p.populationType,
            MAX(v.injectionDate),
            DATEDIFF(CURRENT_DATE, MAX(v.injectionDate))
        )
        FROM Patient p
        JOIN p.visits v
        GROUP BY p.patientId, p.sex, p.populationType
        HAVING DATEDIFF(CURRENT_DATE, MAX(v.injectionDate)) > 90
        ORDER BY MAX(v.injectionDate) ASC
        """)
    List<MissingFollowUpDTO> getMissingFollowUps();
}