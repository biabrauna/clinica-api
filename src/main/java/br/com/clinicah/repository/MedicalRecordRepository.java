package br.com.clinicah.repository;

import br.com.clinicah.model.MedicalRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Integer> {

    Page<MedicalRecord> findByPatientIdOrderByRecordDateDesc(Integer patientId, Pageable pageable);

    boolean existsByIdAndDoctorId(Integer id, Integer doctorId);
}
