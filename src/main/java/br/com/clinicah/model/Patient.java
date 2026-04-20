package br.com.clinicah.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "CadastroPac")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nome")
    private String name;

    @Column(name = "telefone")
    private String phone;

    @Column(name = "dataNascimento")
    private LocalDate birthDate;

    @Column(name = "endereco")
    private String address;

    @Column(name = "complemento")
    private String complement;

    @Column(name = "bairro")
    private String neighborhood;

    @Column(name = "cep")
    private String zipCode;

    @Column(name = "UF")
    private String state;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getComplement() { return complement; }
    public void setComplement(String complement) { this.complement = complement; }
    public String getNeighborhood() { return neighborhood; }
    public void setNeighborhood(String neighborhood) { this.neighborhood = neighborhood; }
    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
}
