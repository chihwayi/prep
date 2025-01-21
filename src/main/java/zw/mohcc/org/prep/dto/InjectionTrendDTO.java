package zw.mohcc.org.prep.dto;

import java.time.LocalDate;
import java.time.YearMonth;

public class InjectionTrendDTO {
    private YearMonth month;
    private Long totalInjections;

    public InjectionTrendDTO(LocalDate month, Long totalInjections) {
        this.month = YearMonth.from(month);
        this.totalInjections = totalInjections;
    }

    public YearMonth getMonth() {
        return month;
    }

    public void setMonth(YearMonth month) {
        this.month = month;
    }

    public Long getTotalInjections() {
        return totalInjections;
    }

    public void setTotalInjections(Long totalInjections) {
        this.totalInjections = totalInjections;
    }
}