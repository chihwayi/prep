package zw.mohcc.org.prep.dto;

import zw.mohcc.org.prep.enums.CurrentStatus;

public class RetentionDTO {
    private String typeOfInjection;
    private CurrentStatus currentStatus;
    private String discontinuationReason;
    private Long totalCases;

    public RetentionDTO(String typeOfInjection, CurrentStatus currentStatus, String discontinuationReason, Long totalCases) {
        this.typeOfInjection = typeOfInjection;
        this.currentStatus = currentStatus;
        this.discontinuationReason = discontinuationReason;
        this.totalCases = totalCases;
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

    public Long getTotalCases() {
        return totalCases;
    }

    public void setTotalCases(Long totalCases) {
        this.totalCases = totalCases;
    }
}
