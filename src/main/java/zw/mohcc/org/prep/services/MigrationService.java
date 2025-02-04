package zw.mohcc.org.prep.services;

import zw.mohcc.org.prep.dto.MigrationResult;
import zw.mohcc.org.prep.entities.Patient;
import zw.mohcc.org.prep.entities.Visit;
import zw.mohcc.org.prep.enums.*;
import zw.mohcc.org.prep.repositories.PatientRepository;
import zw.mohcc.org.prep.repositories.VisitRepository;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class MigrationService {

    private static final Logger logger = Logger.getLogger(MigrationService.class.getName());

    private final PatientRepository patientRepository;
    private final VisitRepository visitRepository;

    @Autowired
    public MigrationService(PatientRepository patientRepository, VisitRepository visitRepository) {
        this.patientRepository = patientRepository;
        this.visitRepository = visitRepository;
    }

    public MigrationResult migrateData(MultipartFile file) throws IOException {
        MigrationResult result = new MigrationResult();
        int patientsProcessed = 0;
        int visitsProcessed = 0;

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue; // Skip header row
                }

                try {
                    String prepNumber = getCellValue(row.getCell(0));
                    String dob = getCellValue(row.getCell(1));
                    String sex = getCellValue(row.getCell(2));
                    String populationType = getCellValue(row.getCell(3));

                    // Skip rows with empty required fields
                    if (prepNumber.isEmpty() || dob.isEmpty() || sex.isEmpty() || populationType.isEmpty()) {
                        result.addError("Row " + (row.getRowNum() + 1) + ": Missing required fields");
                        continue;
                    }

                    Patient patient = new Patient();
                    patient.setPatientId(UUID.randomUUID().toString());
                    patient.setPrepNumber(prepNumber);
                    patient.setDob(LocalDate.parse(dob));
                    patient.setSex(Sex.valueOf(sex.substring(0, 1).toUpperCase() + sex.substring(1).toLowerCase()));
                    patient.setPopulationType(PopulationType.valueOf(populationType));
                    patient.setCreatedAt(LocalDateTime.now());

                    patientRepository.save(patient);
                    patientsProcessed++;

                    String injectionDate = getCellValue(row.getCell(4));
                    String typeOfInjection = getCellValue(row.getCell(5));
                    String currentStatus = getCellValue(row.getCell(6));
                    String discontinuationReason = getCellValue(row.getCell(7));
                    String adverseEventSeverity = getCellValue(row.getCell(8));
                    String prepExperienceStatus = getCellValue(row.getCell(9));

                    Visit visit = new Visit();
                    visit.setVisitId(UUID.randomUUID().toString());
                    visit.setPatient(patient);
                    visit.setInjectionDate(LocalDate.parse(injectionDate));
                    visit.setTypeOfInjection(typeOfInjection);
                    visit.setCurrentStatus(CurrentStatus.valueOf(currentStatus));
                    visit.setDiscontinuationReason(discontinuationReason);
                    visit.setAdverseEventSeverity(AdverseEventSeverity.valueOf(adverseEventSeverity));
                    visit.setPrepExperienceStatus(PrepExperienceStatus.fromDisplayName(prepExperienceStatus));
                    visit.setCreatedAt(LocalDateTime.now());

                    visitRepository.save(visit);
                    visitsProcessed++;

                } catch (IllegalArgumentException | DateTimeParseException e) {
                    result.addError("Row " + (row.getRowNum() + 1) + ": " + e.getMessage());
                }
            }
        }

        result.setPatientsProcessed(patientsProcessed);
        result.setVisitsProcessed(visitsProcessed);
        result.setSuccessful(patientsProcessed > 0 || result.getErrors().isEmpty());
        result.setMessage(patientsProcessed > 0 ?
                "Successfully processed " + patientsProcessed + " patients and " + visitsProcessed + " visits" :
                "No data was processed");

        return result;
    }

    private String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toLocalDate().toString();
                } else {
                    return String.valueOf((int)cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
}