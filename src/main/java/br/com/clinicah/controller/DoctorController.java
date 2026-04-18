package br.com.clinicah.controller;

import br.com.clinicah.model.Doctor;
import br.com.clinicah.repository.DoctorRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/doctors")
public class DoctorController {

    private final DoctorRepository repository;

    public DoctorController(DoctorRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Doctor> findAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Doctor> findById(@PathVariable Integer id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Doctor save(@RequestBody Doctor doctor) {
        return repository.save(doctor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Doctor> update(@PathVariable Integer id, @RequestBody Doctor data) {
        return repository.findById(id).map(doctor -> {
            doctor.setName(data.getName());
            doctor.setEmail(data.getEmail());
            doctor.setSpecialty(data.getSpecialty());
            doctor.setCrm(data.getCrm());
            return ResponseEntity.ok(repository.save(doctor));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (!repository.existsById(id)) return ResponseEntity.notFound().build();
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
