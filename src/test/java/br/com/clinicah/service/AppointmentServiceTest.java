package br.com.clinicah.service;

import br.com.clinicah.dto.AppointmentRequest;
import br.com.clinicah.dto.AppointmentResponse;
import br.com.clinicah.dto.DaySlots;
import br.com.clinicah.exception.ResourceNotFoundException;
import br.com.clinicah.model.*;
import br.com.clinicah.repository.AppointmentRepository;
import br.com.clinicah.repository.DoctorRepository;
import br.com.clinicah.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AppointmentService — testes unitários")
class AppointmentServiceTest {

    @Mock private AppointmentRepository repository;
    @Mock private DoctorRepository doctorRepository;
    @Mock private PatientRepository patientRepository;

    @InjectMocks
    private AppointmentService service;

    private Doctor doctor;
    private Patient patient;
    private Appointment appointment;

    @BeforeEach
    void setUp() {
        doctor = new Doctor();
        doctor.setId(1);
        doctor.setName("Dra. Ana Lima");
        doctor.setEmail("ana@clinica.com");
        doctor.setSpecialty("Cardiologia");
        doctor.setCrm("CRM/RJ 123456");

        patient = new Patient();
        patient.setId(1);
        patient.setName("João Silva");
        patient.setPhone("(21) 99999-0000");

        appointment = new Appointment();
        appointment.setId(1);
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setScheduledAt(LocalDateTime.of(2027, 6, 10, 9, 0));
        appointment.setStatus(AppointmentStatus.AGENDADA);
    }

    // ── create ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("create agenda consulta com sucesso quando não há conflito")
    void create_semConflito_agendaComSucesso() {
        AppointmentRequest req = buildRequest(1, 1, LocalDateTime.of(2027, 6, 10, 9, 0));
        when(doctorRepository.findById(1)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(1)).thenReturn(Optional.of(patient));
        when(repository.existsByDoctorIdAndScheduledAt(1, req.getScheduledAt())).thenReturn(false);
        when(repository.save(any(Appointment.class))).thenReturn(appointment);

        AppointmentResponse result = service.create(req);

        assertThat(result.getId()).isEqualTo(1);
        verify(repository).save(any(Appointment.class));
    }

    @Test
    @DisplayName("create lança exceção quando médico já tem consulta no horário")
    void create_comConflito_lancaExcecao() {
        AppointmentRequest req = buildRequest(1, 1, LocalDateTime.of(2027, 6, 10, 9, 0));
        when(doctorRepository.findById(1)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(1)).thenReturn(Optional.of(patient));
        when(repository.existsByDoctorIdAndScheduledAt(1, req.getScheduledAt())).thenReturn(true);

        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("consulta agendada");
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("create lança ResourceNotFoundException quando médico não existe")
    void create_medicoInexistente_lancaExcecao() {
        AppointmentRequest req = buildRequest(99, 1, LocalDateTime.of(2027, 6, 10, 9, 0));
        when(doctorRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("create lança ResourceNotFoundException quando paciente não existe")
    void create_pacienteInexistente_lancaExcecao() {
        AppointmentRequest req = buildRequest(1, 99, LocalDateTime.of(2027, 6, 10, 9, 0));
        when(doctorRepository.findById(1)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ── getAvailableSlots ─────────────────────────────────────────────────────

    @Test
    @DisplayName("getAvailableSlots retorna dias com horários livres, excluindo domingos")
    void getAvailableSlots_retornaDiasComSlots() {
        when(doctorRepository.existsById(1)).thenReturn(true);
        when(repository.findByDoctorIdAndScheduledAtBetweenAndStatusNot(
                eq(1), any(), any(), eq(AppointmentStatus.CANCELADA)))
                .thenReturn(List.of());

        // Junho 2027 tem 30 dias. 1/6 é terça-feira.
        List<DaySlots> result = service.getAvailableSlots(1, 2027, 6);

        // Não deve haver domingos
        result.forEach(day -> {
            java.time.LocalDate date = java.time.LocalDate.parse(day.getDate());
            assertThat(date.getDayOfWeek()).isNotEqualTo(java.time.DayOfWeek.SUNDAY);
        });
        // Cada dia deve ter slots entre 8h e 19h
        result.forEach(day ->
                assertThat(day.getAvailableSlots()).allMatch(s -> s.matches("\\d{2}:00"))
        );
    }

    @Test
    @DisplayName("getAvailableSlots exclui horários já ocupados")
    void getAvailableSlots_excluiOcupados() {
        when(doctorRepository.existsById(1)).thenReturn(true);

        // Consulta agendada para 10/06/2027 às 09:00
        Appointment booked = new Appointment();
        booked.setScheduledAt(LocalDateTime.of(2027, 6, 10, 9, 0));
        booked.setStatus(AppointmentStatus.AGENDADA);

        when(repository.findByDoctorIdAndScheduledAtBetweenAndStatusNot(
                eq(1), any(), any(), eq(AppointmentStatus.CANCELADA)))
                .thenReturn(List.of(booked));

        List<DaySlots> result = service.getAvailableSlots(1, 2027, 6);

        // O dia 10/06/2027 não deve conter 09:00
        result.stream()
                .filter(d -> d.getDate().equals("2027-06-10"))
                .findFirst()
                .ifPresent(day -> assertThat(day.getAvailableSlots()).doesNotContain("09:00"));
    }

    @Test
    @DisplayName("getAvailableSlots lança exceção quando médico não existe")
    void getAvailableSlots_medicoInexistente_lancaExcecao() {
        when(doctorRepository.existsById(99)).thenReturn(false);

        assertThatThrownBy(() -> service.getAvailableSlots(99, 2027, 6))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── delete ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("delete remove quando consulta existe")
    void delete_existente_remove() {
        when(repository.existsById(1)).thenReturn(true);
        service.delete(1);
        verify(repository).deleteById(1);
    }

    @Test
    @DisplayName("delete lança exceção quando consulta não existe")
    void delete_naoExistente_lancaExcecao() {
        when(repository.existsById(99)).thenReturn(false);
        assertThatThrownBy(() -> service.delete(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private AppointmentRequest buildRequest(int doctorId, int patientId, LocalDateTime scheduledAt) {
        AppointmentRequest req = new AppointmentRequest();
        req.setDoctorId(doctorId);
        req.setPatientId(patientId);
        req.setScheduledAt(scheduledAt);
        return req;
    }
}
