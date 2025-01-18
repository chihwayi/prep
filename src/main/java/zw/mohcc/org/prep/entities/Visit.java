package zw.mohcc.org.prep.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import zw.mohcc.org.prep.enums.AdverseEventSeverity;
import zw.mohcc.org.prep.enums.CurrentStatus;
import zw.mohcc.org.prep.enums.PrepExperienceStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Visit {
    @Id
    private String visitId;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "patient_id")
    @JsonBackReference
    private Patient patient;

    private LocalDate injectionDate;
    private String typeOfInjection;

    @Enumerated(EnumType.STRING)
    private CurrentStatus currentStatus;

    private String discontinuationReason;

    @Enumerated(EnumType.STRING)
    private AdverseEventSeverity adverseEventSeverity;

    @Enumerated(EnumType.STRING)
    private PrepExperienceStatus prepExperienceStatus;
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (visitId == null) {
            visitId = UUID.randomUUID().toString();
        }
        createdAt = LocalDateTime.now();
    }

    public String getVisitId() {
        return visitId;
    }

    public void setVisitId(String visitId) {
        this.visitId = visitId;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public LocalDate getInjectionDate() {
        return injectionDate;
    }

    public void setInjectionDate(LocalDate injectionDate) {
        this.injectionDate = injectionDate;
    }

    public String getTypeOfInjection() {
        return typeOfInjection;
    }

    public void setTypeOfInjection(String typeOfInjection) {
        this.typeOfInjection = typeOfInjection;
    }

    public CurrentStatus getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(CurrentStatus currentStatus) {
        this.currentStatus = currentStatus;
    }

    public String getDiscontinuationReason() {
        return discontinuationReason;
    }

    public void setDiscontinuationReason(String discontinuationReason) {
        this.discontinuationReason = discontinuationReason;
    }

    public AdverseEventSeverity getAdverseEventSeverity() {
        return adverseEventSeverity;
    }

    public void setAdverseEventSeverity(AdverseEventSeverity adverseEventSeverity) {
        this.adverseEventSeverity = adverseEventSeverity;
    }

    public PrepExperienceStatus getPrepExperienceStatus() {
        return prepExperienceStatus;
    }

    public void setPrepExperienceStatus(PrepExperienceStatus prepExperienceStatus) {
        this.prepExperienceStatus = prepExperienceStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}