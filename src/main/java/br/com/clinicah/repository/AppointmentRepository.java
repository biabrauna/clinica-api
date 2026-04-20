package br.com.clinicah.repository;

import br.com.clinicah.model.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

    // detecta conflito: médico já tem consulta naquele horário exato
    boolean existsByDoctorIdAndScheduledAt(Integer doctorId, LocalDateTime scheduledAt);

    // ao atualizar, exclui a própria consulta da checagem
    boolean existsByDoctorIdAndScheduledAtAndIdNot(Integer doctorId, LocalDateTime scheduledAt, Integer id);

    // agenda mensal do médico
    Page<Appointment> findByDoctorIdAndScheduledAtBetweenOrderByScheduledAt(
            Integer doctorId, LocalDateTime start, LocalDateTime end, Pageable pageable);
}
