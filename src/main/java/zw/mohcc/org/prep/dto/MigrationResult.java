package zw.mohcc.org.prep.dto;

import java.util.ArrayList;
import java.util.List;

public class MigrationResult {
    private int patientsProcessed = 0;
    private int visitsProcessed = 0;
    private List<String> errors = new ArrayList<>();

    public void incrementPatientsProcessed() {
        patientsProcessed++;
    }

    public void incrementVisitsProcessed() {
        visitsProcessed++;
    }

    public void addError(String error) {
        errors.add(error);
    }

    public int getPatientsProcessed() {
        return patientsProcessed;
    }

    public int getVisitsProcessed() {
        return visitsProcessed;
    }

    public List<String> getErrors() {
        return errors;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public void addWarning(String s) {
    }
}