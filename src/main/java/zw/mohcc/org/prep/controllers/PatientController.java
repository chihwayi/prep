package zw.mohcc.org.prep.controllers;

import org.springframework.http.ResponseEntity;
import zw.mohcc.org.prep.entities.Patient;
import zw.mohcc.org.prep.entities.Visit;
import zw.mohcc.org.prep.services.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import java.util.Map;
import java.util.Collections;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    @Autowired
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping("/register")
    public Patient registerPatient(@RequestBody Patient patient) {
        System.out.println("Received Patient: " + patient);
        return patientService.registerPatient(patient);
    }

    @GetMapping("/patientid/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable String id) {
        return patientService.findByPatientId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/prepnumber/{id}")
    public ResponseEntity<Optional<Patient>> getPatientByPrepNumber(@PathVariable String id) {
        Optional<Patient> patient = patientService.findByPrepNumber(id);
        return ResponseEntity.ok(patient);
    }

    @GetMapping("/{prepNumber}/visits")
    public ResponseEntity<List<Visit>> getVisitsByPrepNumber(@PathVariable String prepNumber) {
        List<Visit> visits = patientService.getVisitsByPrepNumber(prepNumber);
        return ResponseEntity.ok(visits);
    }

    @GetMapping
    public ResponseEntity<Page<Patient>> getPatients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "prepNumber") String sort,
            @RequestParam(defaultValue = "asc") String order,
            @RequestParam(required = false) String filter) {

        Sort.Direction direction = order.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sorting = Sort.by(direction, sort);
        PageRequest pageRequest = PageRequest.of(page, pageSize, sorting);

        Page<Patient> patients;
        if (filter != null && !filter.trim().isEmpty()) {
            // Search with filter
            patients = patientService.findPatientsWithFilter(filter, pageRequest);
        } else {
            // Get all patients without filter
            patients = patientService.findAllPatients(pageRequest);
        }

        return ResponseEntity.ok(patients);
    }

    @GetMapping("/check/{prepNumber}")
    public ResponseEntity<Map<String, Boolean>> checkPatientExists(@PathVariable String prepNumber) {
        boolean exists = patientService.findByPrepNumber(prepNumber).isPresent();
        Map<String, Boolean> response = Collections.singletonMap("exists", exists);
        return ResponseEntity.ok(response);
    }
}
