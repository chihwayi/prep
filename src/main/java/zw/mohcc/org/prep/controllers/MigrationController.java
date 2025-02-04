package zw.mohcc.org.prep.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import zw.mohcc.org.prep.utils.ExcelDataMigrationTool;
import zw.mohcc.org.prep.dto.MigrationResult;

@RestController
@RequestMapping("/api/migrate")
@CrossOrigin
public class MigrationController {

    private final ExcelDataMigrationTool migrationTool;

    @Autowired
    public MigrationController(ExcelDataMigrationTool migrationTool) {
        this.migrationTool = migrationTool;
    }

    @PostMapping("/import-excel")
    public ResponseEntity<MigrationResult> importExcelData(@RequestParam("file") MultipartFile file) {
        try {
            MigrationResult result = migrationTool.migrateData(file.getInputStream());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            MigrationResult errorResult = new MigrationResult();
            errorResult.setSuccessful(false);
            errorResult.setMessage("Failed to process file: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResult);
        }
    }
}