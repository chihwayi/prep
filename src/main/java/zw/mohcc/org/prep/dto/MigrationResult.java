package zw.mohcc.org.prep.dto;

import java.util.List;
import java.util.ArrayList;

public class MigrationResult {
    private boolean successful;
    private String message;
    private int patientsProcessed;
    private int visitsProcessed;
    private List<String> errors;

    public MigrationResult() {
        this.errors = new ArrayList<>();
    }

    // Getters and Setters
    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getPatientsProcessed() {
        return patientsProcessed;
    }

    public void setPatientsProcessed(int patientsProcessed) {
        this.patientsProcessed = patientsProcessed;
    }

    public int getVisitsProcessed() {
        return visitsProcessed;
    }

    public void setVisitsProcessed(int visitsProcessed) {
        this.visitsProcessed = visitsProcessed;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public void addError(String error) {
        this.errors.add(error);
    }
}