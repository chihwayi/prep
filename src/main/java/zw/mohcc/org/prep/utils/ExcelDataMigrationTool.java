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

        try (Workbook workbook = new XSSFWorkbook(excelFileStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Map<String, Patient> processedPatients = new HashMap<>();

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header row

                try {
                    processRow(row, processedPatients, result);
                } catch (Exception e) {
                    result.addError("Error processing row " + row.getRowNum() + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            result.addError("Failed to process Excel file: " + e.getMessage());
        }

        return result;
    }

    private void processRow(Row row, Map<String, Patient> processedPatients, MigrationResult result) {
        String prepNumber = getCellValueAsString(row.getCell(0));

        // Check if patient already exists in the database
        Patient patient = processedPatients.get(prepNumber);
        if (patient == null) {
            patient = patientRepository.findByPrepNumber(prepNumber).orElse(null);

            if (patient == null) {
                patient = createPatient(row);
                try {
                    patient = patientRepository.save(patient);
                    processedPatients.put(prepNumber, patient);
                    result.incrementPatientsProcessed();
                } catch (Exception e) {
                    result.addError("Failed to save patient with PrEP number " + prepNumber + ": " + e.getMessage());
                    return;
                }
            } else {
                processedPatients.put(prepNumber, patient);
            }
        }

        // Create and save visit
        Visit visit = createVisit(row, patient);

        // Check if visit is a duplicate
        if (!visitRepository.existsByInjectionDateAndPatient(visit.getInjectionDate(), visit.getPatient())) {
            try {
                visitRepository.save(visit);
                result.incrementVisitsProcessed();
            } catch (Exception e) {
                result.addError("Failed to save visit for patient " + prepNumber + ": " + e.getMessage());
            }
        } else {
            result.addError("Duplicate visit found for patient " + prepNumber + " on date " + visit.getInjectionDate());
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