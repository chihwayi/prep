package zw.mohcc.org.prep.dto;

import zw.mohcc.org.prep.enums.PopulationType;
import zw.mohcc.org.prep.enums.Sex;

public class DemographicDTO {
    private Sex sex;
    private PopulationType populationType;
    private String ageGroup;
    private Long totalPatients;

    public DemographicDTO(Sex sex, PopulationType populationType, String ageGroup, Long totalPatients) {
        this.sex = sex;
        this.populationType = populationType;
        this.ageGroup = ageGroup;
        this.totalPatients = totalPatients;
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

    public Long getTotalPatients() {
        return totalPatients;
    }

    public void setTotalPatients(Long totalPatients) {
        this.totalPatients = totalPatients;
    }

    public String getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }
}
