package com.doctors.backend.services.impl;

import com.doctors.backend.entity.Message;
import com.doctors.backend.entity.Specialty;
import com.doctors.backend.entity.User;
import com.doctors.backend.repositories.MessageRepository;
import com.doctors.backend.repositories.RoleRepository;
import com.doctors.backend.repositories.SpecialtyRepository;
import com.doctors.backend.repositories.UserRepository;
import com.doctors.backend.services.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorServiceImpl implements DoctorService {

    @Autowired
    private UserRepository doctorRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<User> getDoctors(){
        return doctorRepo.findAll();
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return doctorRepo.findAll(pageable);
    }


    @Override
    public User getDoctor(Long id){
        return doctorRepo.findById(id).orElse(null);
    }

    @Override
    public User registerDoctor(User doctor) {
        User newDoctor = new User();

        newDoctor.setFirstname(doctor.getFirstname());
        newDoctor.setLastname(doctor.getLastname());
        newDoctor.setEmail(doctor.getEmail());
        newDoctor.setPassword(passwordEncoder.encode(doctor.getPassword()));
        newDoctor.setPhoto(doctor.getPhoto());
        newDoctor.setPhone(doctor.getPhone());
        newDoctor.setSex(doctor.getSex());
        newDoctor.setSpecialty(doctor.getSpecialty());
        newDoctor.setRoles(roleRepo.findByName("ROLE_DOCTOR"));

        return doctorRepo.save(newDoctor);
    }

    @Override
    public User updatedDoctor(User doctor, Long id) {
        User updatedDoctor = doctorRepo.findById(id).orElse(null);

        updatedDoctor.setFirstname(doctor.getFirstname());
        updatedDoctor.setLastname(doctor.getLastname());
        updatedDoctor.setEmail(doctor.getEmail());
        updatedDoctor.setPhoto(doctor.getPhoto());
        updatedDoctor.setPhone(doctor.getPhone());
        updatedDoctor.setSpecialty(doctor.getSpecialty());
        return doctorRepo.save(updatedDoctor);
    }


    @Override
    public Boolean existsByEmail(String email) {
        return doctorRepo.existsByEmail(email);
    }


                                //specialties
    @Override
    public List<Specialty> findAllSpecialties(){
        return specialtyRepository.findAll();
    }

                                //Messages (Form)
    @Override
    public Message saveMessage(Message message, User user){
        Message newMessage = new Message();

        newMessage.setFirstname(message.getFirstname());
        newMessage.setLastname(message.getLastname());
        newMessage.setEmail(message.getEmail());
        newMessage.setReason(message.getReason());
        newMessage.setMessage(message.getMessage());
        newMessage.setUser(user);

        return messageRepository.save(newMessage);
    }

    @Override
    public void deleteDoctor(Long id) {
        doctorRepo.deleteById(id);
    }

    @Override
    public User findByEmail(String email){
        return doctorRepo.findByEmail(email);
    }
}
