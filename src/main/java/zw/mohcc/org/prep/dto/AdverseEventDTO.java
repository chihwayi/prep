package zw.mohcc.org.prep.dto;

import zw.mohcc.org.prep.enums.AdverseEventSeverity;
import zw.mohcc.org.prep.enums.PopulationType;

public class AdverseEventDTO {
    private PopulationType populationType;
    private AdverseEventSeverity severity;
    private Long totalEvents;

    public AdverseEventDTO(PopulationType populationType, AdverseEventSeverity severity, Long totalEvents) {
        this.populationType = populationType;
        this.severity = severity;
        this.totalEvents = totalEvents;
    }

    public PopulationType getPopulationType() {
        return populationType;
    }

    public void setPopulationType(PopulationType populationType) {
        this.populationType = populationType;
    }

    public AdverseEventSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(AdverseEventSeverity severity) {
        this.severity = severity;
    }

    public Long getTotalEvents() {
        return totalEvents;
    }

    public void setTotalEvents(Long totalEvents) {
        this.totalEvents = totalEvents;
    }
}
