package zw.mohcc.org.prep.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import zw.mohcc.org.prep.dto.MigrationResult;
import zw.mohcc.org.prep.entities.*;
import zw.mohcc.org.prep.enums.*;
import zw.mohcc.org.prep.repositories.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class ExcelDataMigrationTool {
    private final PatientRepository patientRepository;
    private final VisitRepository visitRepository;

    public ExcelDataMigrationTool(PatientRepository patientRepository, VisitRepository visitRepository) {
        this.patientRepository = patientRepository;
        this.visitRepository = visitRepository;
    }

    @Transactional
    public MigrationResult migrateData(InputStream excelFileStream) {
        MigrationResult result = new MigrationResult();
        result.setPatientsProcessed(0);
        result.setVisitsProcessed(0);

        try (Workbook workbook = new XSSFWorkbook(excelFileStream)) {
            Map<String, Patient> processedPatients = new HashMap<>();
            int numberOfSheets = workbook.getNumberOfSheets();

            if (numberOfSheets == 0) {
                result.addError("No sheets found in the workbook");
                return result;
            }

            // Process each sheet in the workbook
            for (int sheetIndex = 0; sheetIndex < numberOfSheets; sheetIndex++) {
                Sheet sheet = workbook.getSheetAt(sheetIndex);
                String sheetName = sheet.getSheetName();

                try {
                    processSheet(sheet, processedPatients, result, sheetName);
                } catch (Exception e) {
                    result.addError("Failed to process sheet '" + sheetName + "': " + e.getMessage());
                }
            }

            // Set success status based on processing results
            result.setSuccessful(result.getPatientsProcessed() > 0 && result.getErrors().isEmpty());
            result.setMessage(String.format("Processed %d sheets, %d patients and %d visits %s",
                    numberOfSheets,
                    result.getPatientsProcessed(),
                    result.getVisitsProcessed(),
                    result.getErrors().isEmpty() ? "successfully" : "with some errors"));

        } catch (Exception e) {
            result.setSuccessful(false);
            result.addError("Failed to process Excel file: " + e.getMessage());
            result.setMessage("Failed to process Excel file");
        }

        return result;
    }

    private void processSheet(Sheet sheet, Map<String, Patient> processedPatients, MigrationResult result, String sheetName) {
        // Validate sheet structure
        if (!isValidSheetStructure(sheet)) {
            result.addError("Sheet '" + sheetName + "' does not have the expected column structure");
            return;
        }

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue; // Skip header row

            try {
                processRow(row, processedPatients, result, sheetName);
            } catch (Exception e) {
                result.addError(String.format("Sheet '%s', Row %d: %s",
                        sheetName, row.getRowNum() + 1, e.getMessage()));
            }
        }
    }

    private boolean isValidSheetStructure(Sheet sheet) {
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) return false;

        // Define expected headers (adjust these based on your actual headers)
        String[] expectedHeaders = {
                "ID(PrEP Number)", "DOB", "Sex(M,F)", "Population Type(SW,MSM, TG, AGYW…)",
                "PrEP Experience Status (Naïve, transitioning OP,  transitioning DVR)",
                "Injection  date", "Type of Injection",
                "Active on PrEP-PX,Discontinue-D,Adverse event-AE, Adverse event & Discontinue-AED, Missed visit-MV, Switched-SWD",
                "Discontinuation reason or Adverse event type", "AE Severity (mild, moderate, severe)"
        };

        // Check if all expected headers are present
        for (int i = 0; i < expectedHeaders.length; i++) {
            Cell cell = headerRow.getCell(i);
            if (cell == null || !cell.getStringCellValue().trim().equalsIgnoreCase(expectedHeaders[i])) {
                return false;
            }
        }

        return true;
    }

    private void processRow(Row row, Map<String, Patient> processedPatients, MigrationResult result, String sheetName) {
        String prepNumber = getCellValueAsString(row.getCell(0));
        if (prepNumber == null || prepNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Missing PrEP number");
        }

        // Process patient data
        Patient patient = processedPatients.get(prepNumber);
        if (patient == null) {
            patient = patientRepository.findByPrepNumber(prepNumber).orElse(null);

            if (patient == null) {
                patient = createPatient(row);
                try {
                    patient = patientRepository.save(patient);
                    processedPatients.put(prepNumber, patient);
                    result.setPatientsProcessed(result.getPatientsProcessed() + 1);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to save patient: " + e.getMessage());
                }
            } else {
                processedPatients.put(prepNumber, patient);
            }
        }

        // Process visit data
        Visit visit = createVisit(row, patient);
        if (visit.getInjectionDate() == null) {
            throw new IllegalArgumentException("Missing or invalid injection date");
        }

        // Check for duplicate visits
        if (!visitRepository.existsByInjectionDateAndPatient(visit.getInjectionDate(), visit.getPatient())) {
            try {
                visitRepository.save(visit);
                result.setVisitsProcessed(result.getVisitsProcessed() + 1);
            } catch (Exception e) {
                throw new RuntimeException("Failed to save visit: " + e.getMessage());
            }
        } else {
            result.addError(String.format("Sheet '%s', Row %d: Duplicate visit found for patient %s on date %s",
                    sheetName, row.getRowNum() + 1, prepNumber, visit.getInjectionDate()));
        }
    }

    private Patient createPatient(Row row) {
        Patient patient = new Patient();
        patient.setPatientId(UUID.randomUUID().toString());
        patient.setPrepNumber(getCellValueAsString(row.getCell(0)));
        patient.setDob(getLocalDateFromCell(row.getCell(1)));
        patient.setSex(mapSex(getCellValueAsString(row.getCell(2))));
        patient.setPopulationType(mapPopulationType(getCellValueAsString(row.getCell(3))));
        patient.setCreatedAt(LocalDateTime.now());
        return patient;
    }

    private Visit createVisit(Row row, Patient patient) {
        Visit visit = new Visit();
        visit.setVisitId(UUID.randomUUID().toString());
        visit.setPatient(patient);
        visit.setInjectionDate(getLocalDateFromCell(row.getCell(5)));
        visit.setTypeOfInjection(getCellValueAsString(row.getCell(6)));
        visit.setCurrentStatus(mapCurrentStatus(getCellValueAsString(row.getCell(7))));
        visit.setDiscontinuationReason(getCellValueAsString(row.getCell(8)));
        visit.setAdverseEventSeverity(mapAdverseEventSeverity(getCellValueAsString(row.getCell(9))));
        visit.setPrepExperienceStatus(mapPrepExperienceStatus(getCellValueAsString(row.getCell(4))));
        visit.setCreatedAt(LocalDateTime.now());
        return visit;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            default:
                return null;
        }
    }

    private LocalDate getLocalDateFromCell(Cell cell) {
        if (cell == null) return null;
        try {
            return cell.getLocalDateTimeCellValue().toLocalDate();
        } catch (Exception e) {
            return null;
        }
    }

    // Mapping methods for enums
    private Sex mapSex(String value){
        if (value == null) return null;
        Map<String,Sex> sexMap = new HashMap<>();
        sexMap.put("Female",Sex.Female);
        sexMap.put("Male", Sex.Male);
        return sexMap.getOrDefault(value, null);
    }

    private PopulationType mapPopulationType(String value){
        if (value == null) return null;
        Map<String,PopulationType> populationTypeMap = new HashMap<>();
        populationTypeMap.put("SW", PopulationType.SW);
        populationTypeMap.put("MSM", PopulationType.MSM);
        populationTypeMap.put("TG", PopulationType.TG);
        populationTypeMap.put("AGYW", PopulationType.AGYW);
        populationTypeMap.put("Other", PopulationType.OTHER);
        populationTypeMap.put("Gen Pop", PopulationType.GEN_POP);
        populationTypeMap.put("Sero_discordant", PopulationType.SERO_DISCORDANT);
        return populationTypeMap.getOrDefault(value, null);
    }

    private CurrentStatus mapCurrentStatus(String value) {
        if (value == null) return null;
        Map<String, CurrentStatus> statusMap = new HashMap<>();
        statusMap.put("PX", CurrentStatus.PX);
        statusMap.put("D", CurrentStatus.D);
        statusMap.put("AE", CurrentStatus.AE);
        statusMap.put("AED", CurrentStatus.AED);
        statusMap.put("MV", CurrentStatus.MV);
        statusMap.put("SWD", CurrentStatus.SWD);
        return statusMap.getOrDefault(value, null);
    }

    private AdverseEventSeverity mapAdverseEventSeverity(String value) {
        if (value == null) return null;
        Map<String, AdverseEventSeverity> severityMap = new HashMap<>();
        severityMap.put("Mild", AdverseEventSeverity.MILD);
        severityMap.put("Moderate", AdverseEventSeverity.MODERATE);
        severityMap.put("Severe", AdverseEventSeverity.SEVERE);
        return severityMap.getOrDefault(value, null);
    }

    private PrepExperienceStatus mapPrepExperienceStatus(String value) {
        if (value == null) return null;
        Map<String, PrepExperienceStatus> statusMap = new HashMap<>();
        statusMap.put("Naïve", PrepExperienceStatus.NAIVE);
        statusMap.put("Transitioning-OP", PrepExperienceStatus.TRANSITIONING_OP);
        statusMap.put("Transitioning-DVR", PrepExperienceStatus.TRANSITIONING_DVR);
        statusMap.put("CAB-LA followup visit", PrepExperienceStatus.CAB_LA_FOLLOWUP_VISIT);
        return statusMap.getOrDefault(value, null);
    }

}