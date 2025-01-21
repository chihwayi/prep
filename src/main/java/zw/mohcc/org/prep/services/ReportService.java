package zw.mohcc.org.prep.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.mohcc.org.prep.dto.*;
import zw.mohcc.org.prep.repositories.PatientRepository;
import zw.mohcc.org.prep.repositories.VisitRepository;

import java.time.LocalDate;
import java.util.List;

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
        return visitRepository.getInjectionTrends(startDate, endDate);
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
        return visitRepository.getMissingFollowUps();
    }
}
