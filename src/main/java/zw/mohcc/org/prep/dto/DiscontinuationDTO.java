package zw.mohcc.org.prep.dto;

public class DiscontinuationDTO {
    private String ageGroup;
    private String discontinuationReason;
    private Long totalCases;

    public DiscontinuationDTO(String ageGroup, String discontinuationReason, Long totalCases) {
        this.ageGroup = ageGroup;
        this.discontinuationReason = discontinuationReason;
        this.totalCases = totalCases;
    }

    public String getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
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
