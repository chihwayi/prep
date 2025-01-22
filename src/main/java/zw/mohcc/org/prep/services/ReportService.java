package zw.mohcc.org.prep.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.mohcc.org.prep.dto.*;
import zw.mohcc.org.prep.enums.PopulationType;
import zw.mohcc.org.prep.enums.Sex;
import zw.mohcc.org.prep.repositories.PatientRepository;
import zw.mohcc.org.prep.repositories.VisitRepository;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReportService {
    private final PatientRepository patientRepository;
    private final VisitRepository visitRepository;

    @Autowired
    public ReportService(PatientRepository patientRepository, VisitRepository visitRepository) {
        this.patientRepository = patientRepository;
        this.visitRepository = visitRepository;
    }

    // 1. Population Demographics Report
    public List<DemographicDTO> getPopulationDemographics() {
        return patientRepository.getPopulationDemographics();
    }

    // 2. Patient Retention Report
    public List<RetentionDTO> getRetentionByInjectionType() {
        return visitRepository.getRetentionByInjectionType();
    }

    // 3. Injection Trends
    public List<InjectionTrendDTO> getInjectionTrends(LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = visitRepository.getInjectionTrends(startDate, endDate);
        return results.stream()
                .map(row -> new InjectionTrendDTO(
                        YearMonth.of(
                                ((Number) row[0]).intValue(),  // year
                                ((Number) row[1]).intValue()   // month
                        ),
                        ((Number) row[2]).longValue()      // total
                ))
                .collect(Collectors.toList());
    }
    /*
    // 4. PrEP Experience Breakdown
    public List<PrepExperienceDTO> getPrepExperienceBreakdown() {
        return visitRepository.getPrepExperienceBreakdown();
    }

    // 5. Adverse Events by Population
    public List<AdverseEventDTO> getAdverseEventsByPopulation() {
        return visitRepository.getAdverseEventsByPopulation();
    }

    // 6. Gender and Population Type Analysis
    public List<GenderAnalysisDTO> getGenderAnalysis() {
        return patientRepository.getGenderAnalysis();
    }

    // 7. Discontinuation Analysis
    public List<DiscontinuationDTO> getDiscontinuationByAgeGroup() {
        return visitRepository.getDiscontinuationByAgeGroup();
    }

    // 8. Active Patients Report
    public List<ActivePatientsDTO> getActivePatientsByPopulation() {
        return patientRepository.getActivePatientsByPopulation();
    }
 */
    // 9. Missing Follow-ups Report
    public List<MissingFollowUpDTO> getMissingFollowUps() {
        List<Object[]> results = visitRepository.getMissingFollowUps();
        return results.stream()
                .map(row -> {
                    // Convert java.sql.Date to java.time.LocalDate
                    LocalDate lastInjectionDate = ((java.sql.Date) row[3]).toLocalDate();
                    return new MissingFollowUpDTO(
                            (String) row[0],                     // patientId
                            Sex.valueOf((String) row[1]),        // sex
                            PopulationType.valueOf((String) row[2]), // populationType
                            lastInjectionDate,                   // lastInjectionDate
                            ((Number) row[4]).longValue()        // daysSinceLastInjection
                    );
                })
                .collect(Collectors.toList());
    }

}
