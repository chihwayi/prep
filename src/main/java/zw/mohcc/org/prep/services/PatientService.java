package zw.mohcc.org.prep.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import zw.mohcc.org.prep.entities.Patient;
import zw.mohcc.org.prep.entities.Visit;
import zw.mohcc.org.prep.repositories.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    @Autowired
    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public Patient registerPatient(Patient patient) {
        patient.setPatientId(UUID.randomUUID().toString());
        patient.setCreatedAt(LocalDateTime.now());
        return patientRepository.save(patient);
    }

    public Optional<Patient> findByPatientId(String patientId) {
        return patientRepository.findById(patientId);
    }

    public Optional<Patient> findByPrepNumber(String prepNumber) {
        return patientRepository.findByPrepNumber(prepNumber);
    }

    public List<Visit> getVisitsByPrepNumber(String prepNumber) {
        Patient patient = patientRepository.findByPrepNumber(prepNumber).orElseThrow(() -> new RuntimeException("Patient not found with prep number " + prepNumber));
        return patient.getVisits();
    }

    public Page<Patient> findAllPatients(Pageable pageable) {
        return patientRepository.findAll(pageable);
    }

    public Page<Patient> findPatientsWithFilter(String filter, Pageable pageable) {
        return patientRepository.findByPrepNumberContainingIgnoreCase(filter, pageable);
    }

    public List<Map<String, Object>> getVisitSummariesByPrepNumber(String prepNumber) {
        Patient patient = patientRepository.findByPrepNumber(prepNumber).orElseThrow(() -> new RuntimeException("Patient not found with prep number " + prepNumber));
        return patient.getVisits().stream()
                .map(
                        visit -> {
                            Map<String, Object> result = new HashMap<>();
                            result.put("typeOfInjection", visit.getTypeOfInjection());
                            result.put("currentStatus", visit.getCurrentStatus());
                            result.put("discontinuationReason", visit.getDiscontinuationReason());
                            result.put("adverseEventSeverity", visit.getAdverseEventSeverity());
                            result.put("prepExperienceStatus", visit.getVisitId());
                            return result;
                        }
                ).collect(Collectors.toList());
    }
}
