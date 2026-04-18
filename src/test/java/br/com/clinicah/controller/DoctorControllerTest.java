package br.com.clinicah.controller;

import br.com.clinicah.model.Doctor;
import br.com.clinicah.repository.DoctorRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DoctorController.class)
class DoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DoctorRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    private Doctor doctor;

    @BeforeEach
    void setup() {
        doctor = new Doctor();
        doctor.setId(1);
        doctor.setName("Dr. John");
        doctor.setEmail("john@clinic.com");
        doctor.setSpecialty("Cardiology");
        doctor.setCrm("CRM-12345");
    }

    @Test
    void shouldListDoctors() throws Exception {
        when(repository.findAll()).thenReturn(List.of(doctor));

        mockMvc.perform(get("/doctors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Dr. John"))
                .andExpect(jsonPath("$[0].specialty").value("Cardiology"));
    }

    @Test
    void shouldFindDoctorById() throws Exception {
        when(repository.findById(1)).thenReturn(Optional.of(doctor));

        mockMvc.perform(get("/doctors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Dr. John"));
    }

    @Test
    void shouldReturn404WhenDoctorNotFound() throws Exception {
        when(repository.findById(99)).thenReturn(Optional.empty());

        mockMvc.perform(get("/doctors/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldSaveDoctor() throws Exception {
        when(repository.save(any())).thenReturn(doctor);

        mockMvc.perform(post("/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(doctor)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.crm").value("CRM-12345"));
    }

    @Test
    void shouldDeleteDoctor() throws Exception {
        when(repository.existsById(1)).thenReturn(true);

        mockMvc.perform(delete("/doctors/1"))
                .andExpect(status().isNoContent());
    }
}
