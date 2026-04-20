package br.com.clinicah.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "prontuario")
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    // consulta que gerou o prontuário (opcional — pode ser criado sem consulta)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @Column(name = "record_date", nullable = false)
    private LocalDateTime recordDate;

    @Column(name = "queixa_principal", nullable = false, length = 1000)
    private String chiefComplaint;

    @Column(name = "exame_clinico", length = 2000)
    private String clinicalFindings;

    @Column(name = "diagnostico", length = 1000)
    private String diagnosis;

    @Column(name = "plano_tratamento", length = 2000)
    private String treatmentPlan;

    @Column(name = "prescricao", length = 2000)
    private String prescription;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }
    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }
    public Appointment getAppointment() { return appointment; }
    public void setAppointment(Appointment appointment) { this.appointment = appointment; }
    public LocalDateTime getRecordDate() { return recordDate; }
    public void setRecordDate(LocalDateTime recordDate) { this.recordDate = recordDate; }
    public String getChiefComplaint() { return chiefComplaint; }
    public void setChiefComplaint(String chiefComplaint) { this.chiefComplaint = chiefComplaint; }
    public String getClinicalFindings() { return clinicalFindings; }
    public void setClinicalFindings(String clinicalFindings) { this.clinicalFindings = clinicalFindings; }
    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    public String getTreatmentPlan() { return treatmentPlan; }
    public void setTreatmentPlan(String treatmentPlan) { this.treatmentPlan = treatmentPlan; }
    public String getPrescription() { return prescription; }
    public void setPrescription(String prescription) { this.prescription = prescription; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
