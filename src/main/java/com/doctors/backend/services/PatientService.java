package com.doctors.backend.services;

import com.doctors.backend.entity.User;

public interface PatientService {

    User registerPatient(User patient);

    Boolean existsByEmail(String email);
}
