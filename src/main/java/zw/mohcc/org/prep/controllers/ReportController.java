package zw.mohcc.org.prep.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import zw.mohcc.org.prep.dto.*;
import zw.mohcc.org.prep.services.ReportService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/demographics")
    public ResponseEntity<List<DemographicDTO>> getPopulationDemographics() {
        return ResponseEntity.ok(reportService.getPopulationDemographics());
    }

    @GetMapping("/retention")
    public ResponseEntity<List<RetentionDTO>> getRetentionReport() {
        return ResponseEntity.ok(reportService.getRetentionByInjectionType());
    }

    @GetMapping("/injection-trends")
    public ResponseEntity<List<InjectionTrendDTO>> getInjectionTrends(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reportService.getInjectionTrends(startDate, endDate));
    }
/*
    @GetMapping("/prep-experience")
    public ResponseEntity<List<PrepExperienceDTO>> getPrepExperience() {
        return ResponseEntity.ok(reportService.getPrepExperienceBreakdown());
    }

    @GetMapping("/adverse-events")
    public ResponseEntity<List<AdverseEventDTO>> getAdverseEvents() {
        return ResponseEntity.ok(reportService.getAdverseEventsByPopulation());
    }
*/
    @GetMapping("/missing-followups")
    public ResponseEntity<List<MissingFollowUpDTO>> getMissingFollowUps() {
        return ResponseEntity.ok(reportService.getMissingFollowUps());
    }
}
