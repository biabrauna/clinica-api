package br.com.clinicah.controller;

import br.com.clinicah.dto.DoctorRequest;
import br.com.clinicah.model.Doctor;
import br.com.clinicah.model.Role;
import br.com.clinicah.model.User;
import br.com.clinicah.repository.DoctorRepository;
import br.com.clinicah.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("DoctorController — testes de integração")
class DoctorControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired DoctorRepository doctorRepository;
    @Autowired UserRepository userRepository;
    @Autowired PasswordEncoder passwordEncoder;

    private Doctor savedDoctor;

    @BeforeEach
    void setUp() {
        // Garante que existe um usuário ROOT no banco de teste
        if (!userRepository.findByEmail("root@test.com").isPresent()) {
            User root = new User();
            root.setName("Admin");
            root.setEmail("root@test.com");
            root.setPassword(passwordEncoder.encode("senha123"));
            root.setRole(Role.ROOT);
            userRepository.save(root);
        }

        Doctor doctor = new Doctor();
        doctor.setName("Dra. Ana Lima");
        doctor.setEmail("ana@clinica.com");
        doctor.setSpecialty("Cardiologia");
        doctor.setCrm("CRM/RJ 123456");
        savedDoctor = doctorRepository.save(doctor);
    }

    // ── GET /doctors ──────────────────────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ROOT")
    @DisplayName("GET /doctors retorna lista paginada de médicos")
    void getAll_comAutenticacao_retorna200() throws Exception {
        mockMvc.perform(get("/doctors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.content[0].name").isNotEmpty());
    }

    @Test
    @DisplayName("GET /doctors sem autenticação retorna 403")
    void getAll_semAutenticacao_retorna403() throws Exception {
        mockMvc.perform(get("/doctors"))
                .andExpect(status().isForbidden());
    }

    // ── GET /doctors/{id} ─────────────────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ROOT")
    @DisplayName("GET /doctors/{id} retorna médico existente")
    void getById_existente_retorna200() throws Exception {
        mockMvc.perform(get("/doctors/{id}", savedDoctor.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Dra. Ana Lima"))
                .andExpect(jsonPath("$.specialty").value("Cardiologia"));
    }

    @Test
    @WithMockUser(roles = "ROOT")
    @DisplayName("GET /doctors/{id} retorna 404 quando não existe")
    void getById_naoExistente_retorna404() throws Exception {
        mockMvc.perform(get("/doctors/{id}", 99999))
                .andExpect(status().isNotFound());
    }

    // ── POST /doctors ─────────────────────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ROOT")
    @DisplayName("POST /doctors cria médico e retorna 201")
    void create_dadosValidos_retorna201() throws Exception {
        DoctorRequest req = new DoctorRequest();
        req.setName("Dr. Carlos Souza");
        req.setEmail("carlos@clinica.com");
        req.setSpecialty("Neurologia");
        req.setCrm("CRM/SP 654321");

        mockMvc.perform(post("/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Dr. Carlos Souza"))
                .andExpect(jsonPath("$.specialty").value("Neurologia"));
    }

    @Test
    @WithMockUser(roles = "ROOT")
    @DisplayName("POST /doctors retorna 400 quando dados são inválidos")
    void create_dadosInvalidos_retorna400() throws Exception {
        DoctorRequest req = new DoctorRequest();
        // name vazio — viola @NotBlank
        req.setName("");
        req.setEmail("nao-e-email");
        req.setSpecialty("Neurologia");
        req.setCrm("CRM/SP 654321");

        mockMvc.perform(post("/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "PACIENTE")
    @DisplayName("POST /doctors retorna 403 para role PACIENTE")
    void create_roleInsuficiente_retorna403() throws Exception {
        DoctorRequest req = new DoctorRequest();
        req.setName("Dr. X");
        req.setEmail("x@clinica.com");
        req.setSpecialty("Ortopedia");
        req.setCrm("CRM/RJ 111");

        mockMvc.perform(post("/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    // ── DELETE /doctors/{id} ──────────────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ROOT")
    @DisplayName("DELETE /doctors/{id} remove e retorna 204")
    void delete_existente_retorna204() throws Exception {
        mockMvc.perform(delete("/doctors/{id}", savedDoctor.getId()))
                .andExpect(status().isNoContent());
    }

    // ── GET /doctors/specialties ──────────────────────────────────────────────

    @Test
    @WithMockUser(roles = "MEDICO")
    @DisplayName("GET /doctors/specialties retorna lista de especialidades")
    void getSpecialties_retornaLista() throws Exception {
        mockMvc.perform(get("/doctors/specialties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasItem("Cardiologia")));
    }
}
