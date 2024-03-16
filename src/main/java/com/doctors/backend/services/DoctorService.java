package com.doctors.backend.services;

import com.doctors.backend.entity.Message;
import com.doctors.backend.entity.Specialty;
import com.doctors.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DoctorService {

    List<User> getDoctors();

    Page<User> findAll(Pageable pageable);

    User getDoctor(Long id);
    User registerDoctor(User doctor);

    User updatedDoctor(User doctor, Long id);
    Boolean existsByEmail(String email);

    List<Specialty> findAllSpecialties();

    Message saveMessage(Message message, User user);

    void deleteDoctor(Long id);

    User findByEmail(String email);
}
