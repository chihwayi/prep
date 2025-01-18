package zw.mohcc.org.prep.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
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
}