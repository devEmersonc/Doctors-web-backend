package com.doctors.backend.services.impl;

import com.doctors.backend.entity.User;
import com.doctors.backend.repositories.RoleRepository;
import com.doctors.backend.repositories.UserRepository;
import com.doctors.backend.services.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PatientServiceImpl implements PatientService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User getUser(Long id){
        return userRepository.findById(id).orElse(null);
    }


    @Override
    public User registerPatient(User patient) {
        User newPatient = new User();

        newPatient.setFirstname(patient.getFirstname());
        newPatient.setLastname(patient.getLastname());
        newPatient.setEmail(patient.getEmail());
        newPatient.setPassword(passwordEncoder.encode(patient.getPassword()));
        newPatient.setPhoto(patient.getPhoto());
        newPatient.setRoles(roleRepository.findByName("ROLE_PATIENT"));

        return userRepository.save(newPatient);
    }

    @Override
    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User updateUser(User user, Long id){
        User updatedUser = userRepository.findById(id).orElse(null);

        updatedUser.setFirstname(user.getFirstname());
        updatedUser.setLastname(user.getLastname());
        updatedUser.setEmail(user.getEmail());

        return userRepository.save(updatedUser);
    }

    @Override
    public void deletePatient(Long id) {
        userRepository.deleteById(id);
    }
}
