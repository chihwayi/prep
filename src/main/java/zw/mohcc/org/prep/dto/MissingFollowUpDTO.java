package zw.mohcc.org.prep.dto;

import zw.mohcc.org.prep.enums.PopulationType;
import zw.mohcc.org.prep.enums.Sex;

import java.time.LocalDate;

public class MissingFollowUpDTO {
    private String patientId;
    private Sex sex;
    private PopulationType populationType;
    private LocalDate lastInjectionDate;
    private Long daysSinceLastInjection;

    public MissingFollowUpDTO(String patientId, Sex sex, PopulationType populationType, LocalDate lastInjectionDate, Long daysSinceLastInjection) {
        this.patientId = patientId;
        this.sex = sex;
        this.populationType = populationType;
        this.lastInjectionDate = lastInjectionDate;
        this.daysSinceLastInjection = daysSinceLastInjection;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public PopulationType getPopulationType() {
        return populationType;
    }

    public void setPopulationType(PopulationType populationType) {
        this.populationType = populationType;
    }

    public LocalDate getLastInjectionDate() {
        return lastInjectionDate;
    }

    public void setLastInjectionDate(LocalDate lastInjectionDate) {
        this.lastInjectionDate = lastInjectionDate;
    }

    public Long getDaysSinceLastInjection() {
        return daysSinceLastInjection;
    }

    public void setDaysSinceLastInjection(Long daysSinceLastInjection) {
        this.daysSinceLastInjection = daysSinceLastInjection;
    }
}
