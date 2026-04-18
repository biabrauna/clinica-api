package br.com.clinicah.model;

import javax.persistence.*;

@Entity
@Table(name = "CadastroMed")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cli_cod")
    private Integer id;

    @Column(name = "nome")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "especializacao")
    private String specialty;

    @Column(name = "crm")
    private String crm;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }

    public String getCrm() { return crm; }
    public void setCrm(String crm) { this.crm = crm; }
}
