package zw.mohcc.org.prep.services;

import org.springframework.stereotype.Service;
import zw.mohcc.org.prep.repositories.PatientRepository;
import zw.mohcc.org.prep.repositories.VisitRepository;
import zw.mohcc.org.prep.dto.DashboardStats;

import java.time.LocalDate;
import java.util.Map;

@Service
public class DashboardService {
    private final PatientRepository patientRepository;
    private final VisitRepository visitRepository;

    public DashboardService(PatientRepository patientRepository, VisitRepository visitRepository) {
        this.patientRepository = patientRepository;
        this.visitRepository = visitRepository;
    }

    public DashboardStats getDashboardStats() {
        // Get total patients
        long totalPatients = patientRepository.count();

        // Get active patients (patients with active status in their latest visit)
        int activePatients = getActivePatientCount();

        // Get visits this month
        int visitsThisMonth = getVisitsThisMonthCount();

        // Get status distribution
        Map<String, Integer> statusDistribution = getStatusDistribution();

        // Get population type distribution
        Map<String, Integer> populationTypeDistribution = getPopulationTypeDistribution();

        return new DashboardStats(
                (int) totalPatients,
                activePatients,
                visitsThisMonth,
                statusDistribution,
                populationTypeDistribution
        );
    }

    private int getActivePatientCount() {
        LocalDate threeMonthsAgo = LocalDate.now().minusMonths(3);
        return patientRepository.countActivePatients(threeMonthsAgo);
    }

    private int getVisitsThisMonthCount() {
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        return visitRepository.countVisitsBetweenDates(startOfMonth, endOfMonth);
    }

    private Map<String, Integer> getStatusDistribution() {
        return visitRepository.getStatusDistribution();
    }

    private Map<String, Integer> getPopulationTypeDistribution() {
        return patientRepository.getPopulationTypeDistribution();
    }


}

