package zw.mohcc.org.prep.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zw.mohcc.org.prep.entities.Facility;
import zw.mohcc.org.prep.services.FacilityService;

import java.util.List;

@RestController
@RequestMapping("/api/facilities")
public class FacilityController {
    private final FacilityService facilityService;

    public FacilityController(FacilityService facilityService) {
        this.facilityService = facilityService;
    }

    @GetMapping
    public ResponseEntity<List<Facility>> findAll() {
        return ResponseEntity.ok(facilityService.getAllFacilities());
    }

    @GetMapping("/{siteCode}")
    public ResponseEntity<Facility> findBySiteCode(@PathVariable String siteCode) {
        return ResponseEntity.ok(facilityService.getFacilityBySiteId(siteCode));
    }
}
