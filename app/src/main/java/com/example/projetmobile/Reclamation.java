package com.example.projetmobile;

public class Reclamation {
    private String absenceId;
    private String reclamationId;
    private String details;

    // Default constructor required for Firebase
    public Reclamation() {
    }

    // Constructor to initialize the object
    public Reclamation(String absenceId, String reclamationId, String details) {
        this.absenceId = absenceId;
        this.reclamationId = reclamationId;
        this.details = details;
    }

    // Getter and setter methods
    public String getAbsenceId() {
        return absenceId;
    }

    public void setAbsenceId(String absenceId) {
        this.absenceId = absenceId;
    }

    public String getReclamationId() {
        return reclamationId;
    }

    public void setReclamationId(String reclamationId) {
        this.reclamationId = reclamationId;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
