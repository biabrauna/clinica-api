package br.com.clinicah.service;

import br.com.clinicah.dto.DoctorRequest;
import br.com.clinicah.dto.DoctorResponse;
import br.com.clinicah.exception.ResourceNotFoundException;
import br.com.clinicah.model.Doctor;
import br.com.clinicah.repository.DoctorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DoctorService — testes unitários")
class DoctorServiceTest {

    @Mock
    private DoctorRepository repository;

    @InjectMocks
    private DoctorService service;

    private Doctor doctor;
    private DoctorRequest request;

    @BeforeEach
    void setUp() {
        doctor = new Doctor();
        doctor.setId(1);
        doctor.setName("Dra. Ana Lima");
        doctor.setEmail("ana@clinica.com");
        doctor.setSpecialty("Cardiologia");
        doctor.setCrm("CRM/RJ 123456");

        request = new DoctorRequest();
        request.setName("Dra. Ana Lima");
        request.setEmail("ana@clinica.com");
        request.setSpecialty("Cardiologia");
        request.setCrm("CRM/RJ 123456");
    }

    // ── findAll ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("findAll sem filtro retorna página de médicos")
    void findAll_semFiltro_retornaPagina() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Doctor> page = new PageImpl<>(List.of(doctor));
        when(repository.findAll(pageable)).thenReturn(page);

        Page<DoctorResponse> result = service.findAll(null, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Dra. Ana Lima");
        verify(repository).findAll(pageable);
    }

    @Test
    @DisplayName("findAll com especialidade delega para método filtrado")
    void findAll_comEspecialidade_filtraPorEspecialidade() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Doctor> page = new PageImpl<>(List.of(doctor));
        when(repository.findBySpecialtyIgnoreCase("Cardiologia", pageable)).thenReturn(page);

        Page<DoctorResponse> result = service.findAll("Cardiologia", pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(repository).findBySpecialtyIgnoreCase("Cardiologia", pageable);
        verify(repository, never()).findAll(any(Pageable.class));
    }

    // ── findById ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("findById retorna médico quando existe")
    void findById_existe_retornaDto() {
        when(repository.findById(1)).thenReturn(Optional.of(doctor));

        DoctorResponse result = service.findById(1);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getCrm()).isEqualTo("CRM/RJ 123456");
    }

    @Test
    @DisplayName("findById lança ResourceNotFoundException quando não existe")
    void findById_naoExiste_lancaExcecao() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ── create ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("create persiste e retorna o médico criado")
    void create_dadosValidos_salvaERetorna() {
        when(repository.save(any(Doctor.class))).thenReturn(doctor);

        DoctorResponse result = service.create(request);

        assertThat(result.getName()).isEqualTo("Dra. Ana Lima");
        assertThat(result.getSpecialty()).isEqualTo("Cardiologia");
        verify(repository).save(any(Doctor.class));
    }

    // ── update ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("update atualiza campos e persiste")
    void update_existente_atualizaEPersiste() {
        when(repository.findById(1)).thenReturn(Optional.of(doctor));

        DoctorRequest novoRequest = new DoctorRequest();
        novoRequest.setName("Dra. Ana Lima Atualizada");
        novoRequest.setEmail("ana@clinica.com");
        novoRequest.setSpecialty("Neurologia");
        novoRequest.setCrm("CRM/RJ 123456");

        Doctor atualizado = new Doctor();
        atualizado.setId(1);
        atualizado.setName("Dra. Ana Lima Atualizada");
        atualizado.setEmail("ana@clinica.com");
        atualizado.setSpecialty("Neurologia");
        atualizado.setCrm("CRM/RJ 123456");
        when(repository.save(any(Doctor.class))).thenReturn(atualizado);

        DoctorResponse result = service.update(1, novoRequest);

        assertThat(result.getSpecialty()).isEqualTo("Neurologia");
        verify(repository).save(any(Doctor.class));
    }

    @Test
    @DisplayName("update lança exceção quando médico não existe")
    void update_naoExistente_lancaExcecao() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── delete ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("delete remove quando médico existe")
    void delete_existente_remove() {
        when(repository.existsById(1)).thenReturn(true);

        service.delete(1);

        verify(repository).deleteById(1);
    }

    @Test
    @DisplayName("delete lança exceção quando médico não existe")
    void delete_naoExistente_lancaExcecao() {
        when(repository.existsById(99)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(99))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(repository, never()).deleteById(any());
    }

    // ── getSpecialties ────────────────────────────────────────────────────────

    @Test
    @DisplayName("getSpecialties retorna lista de especialidades distintas")
    void getSpecialties_retornaLista() {
        when(repository.findDistinctSpecialties()).thenReturn(List.of("Cardiologia", "Neurologia"));

        List<String> result = service.getSpecialties();

        assertThat(result).containsExactly("Cardiologia", "Neurologia");
    }
}
