package br.com.clinicah.controller;

import br.com.clinicah.dto.MedicalRecordRequest;
import br.com.clinicah.dto.MedicalRecordResponse;
import br.com.clinicah.service.MedicalRecordService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/patients/{patientId}/records")
public class MedicalRecordController {

    private final MedicalRecordService service;

    public MedicalRecordController(MedicalRecordService service) {
        this.service = service;
    }

    @GetMapping
    public Page<MedicalRecordResponse> findAll(
            @PathVariable Integer patientId,
            @PageableDefault(size = 10) Pageable pageable) {
        return service.findByPatient(patientId, pageable);
    }

    @GetMapping("/{recordId}")
    public MedicalRecordResponse findById(
            @PathVariable Integer patientId,
            @PathVariable Integer recordId) {
        return service.findById(patientId, recordId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MedicalRecordResponse create(
            @PathVariable Integer patientId,
            @Valid @RequestBody MedicalRecordRequest request) {
        return service.create(patientId, request);
    }

    @PutMapping("/{recordId}")
    public MedicalRecordResponse update(
            @PathVariable Integer patientId,
            @PathVariable Integer recordId,
            @Valid @RequestBody MedicalRecordRequest request) {
        return service.update(patientId, recordId, request);
    }

    @DeleteMapping("/{recordId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Integer patientId,
            @PathVariable Integer recordId) {
        service.delete(patientId, recordId);
    }
}
