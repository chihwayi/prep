package zw.mohcc.org.prep.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zw.mohcc.org.prep.entities.Visit;
import zw.mohcc.org.prep.repositories.VisitRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class VisitService {

    private final VisitRepository visitRepository;

    @Autowired
    public VisitService(VisitRepository visitRepository) {
        this.visitRepository = visitRepository;
    }

    public Visit addVisit(Visit visit) {
        visit.setVisitId(UUID.randomUUID().toString());
        visit.setCreatedAt(LocalDateTime.now());
        return visitRepository.save(visit);
    }

    public List<Visit> findByPatientId(String patientId) {
        return visitRepository.findByPatient_PatientId(patientId);
    }

    }
