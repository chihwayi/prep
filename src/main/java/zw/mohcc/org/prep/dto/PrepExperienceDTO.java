package zw.mohcc.org.prep.dto;

import zw.mohcc.org.prep.enums.PrepExperienceStatus;

public class PrepExperienceDTO {
    private PrepExperienceStatus status;
    private Long totalPatients;

    public PrepExperienceDTO(PrepExperienceStatus status, Long totalPatients) {
        this.status = status;
        this.totalPatients = totalPatients;
    }

    public PrepExperienceStatus getStatus() {
        return status;
    }

    public void setStatus(PrepExperienceStatus status) {
        this.status = status;
    }

    public Long getTotalPatients() {
        return totalPatients;
    }

    public void setTotalPatients(Long totalPatients) {
        this.totalPatients = totalPatients;
    }
}
