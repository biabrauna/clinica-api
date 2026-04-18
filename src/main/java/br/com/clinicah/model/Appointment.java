package br.com.clinicah.model;

import javax.persistence.*;

@Entity
@Table(name = "Consulta")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nome")
    private String patientName;

    @Column(name = "especialista")
    private String specialist;

    @Column(name = "horario")
    private String schedule;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getSpecialist() { return specialist; }
    public void setSpecialist(String specialist) { this.specialist = specialist; }

    public String getSchedule() { return schedule; }
    public void setSchedule(String schedule) { this.schedule = schedule; }
}
