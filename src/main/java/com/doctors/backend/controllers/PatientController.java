package com.doctors.backend.controllers;

import com.doctors.backend.entity.User;
import com.doctors.backend.services.PatientService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @GetMapping("/patient/{user_id}")
    public User getUser(@PathVariable Long user_id){
        return patientService.getUser(user_id);
    }

    @PostMapping("/patient/register")
    public ResponseEntity<?> registerPatient(@Valid @RequestBody User patient, BindingResult result){
        User newPatient = null;
        Map<String, Object> response = new HashMap<>();

        if(result.hasErrors()){
            List<String> errors = new ArrayList<>();
            for(FieldError err: result.getFieldErrors()){
                errors.add(err.getDefaultMessage());
            }

            response.put("errors", errors);
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }

        if(patientService.existsByEmail(patient.getEmail())){
            response.put("error", "El email ya está en uso.");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try{
            newPatient = patientService.registerPatient(patient);
        }catch (Exception e){
            response.put("error", "Error al realizar el registro del paciente.");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("message", "El paciente se ha registrado con éxito.");
        response.put("patient", newPatient);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @PutMapping("/patient/{user_id}")
    public ResponseEntity<?> updateUser(@Valid @RequestBody User user, BindingResult result, @PathVariable Long user_id){
        User currentUser = patientService.getUser(user_id);
        User updatedUser = null;
        Map<String, Object> response = new HashMap<>();

        if(result.hasErrors()){
            List<String> errors = new ArrayList<>();
            for(FieldError err: result.getFieldErrors()){
                errors.add(err.getDefaultMessage());
            }

            response.put("errors", errors);
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }

        if(currentUser == null){
            response.put("message", "El usuario no existe.");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try{
            updatedUser = patientService.updateUser(user, user_id);
        }catch (Exception e){
            response.put("message", "Error al actualizar al usuario en la base de datos.");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("message", "La informacion del usuario se ha actualizado con éxito.");
        response.put("patient", updatedUser);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @PostMapping("/patient/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile image, @RequestParam("id") Long id) {
        Map<String, Object> response = new HashMap<>();
        User patient = patientService.getUser(id);

        if (!image.isEmpty()) {
            String imageName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename().replace(" ", "");
            Path imagePath = Paths.get("uploads").resolve(imageName).toAbsolutePath();

            try {
                Files.copy(image.getInputStream(), imagePath);
            } catch (IOException e) {
                response.put("message", "Error al subir la imagen.");
                response.put("error", e.getMessage().concat(": ").concat(e.getCause().getMessage()));
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            //Validar si el cliente ya tiene una foto o no, si ya existe, eliminamos la foto anterior y actualizamos por la nueva
            String namePreviousImage = patient.getPhoto();

            if (namePreviousImage != null && namePreviousImage.length() > 0) {
                Path previousImageRoute = Paths.get("uploads").resolve(namePreviousImage).toAbsolutePath();
                File previousImageFile = previousImageRoute.toFile();

                if (previousImageFile.exists() && previousImageFile.canRead()) {
                    previousImageFile.delete();
                }
            }

            patient.setPhoto(imageName);
            patientService.updateUser(patient, id);

            response.put("message", "Se ha actualizado la imagen con éxito.");
            response.put("patient", patient);
        }

        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @GetMapping("/patient/uploads/img/{imageName:.+}")
    public ResponseEntity<Resource> viewImage(@PathVariable String imageName) {

        Path filePath = Paths.get("uploads").resolve(imageName).toAbsolutePath();
        Resource recurso = null;

        try {
            recurso = new UrlResource(filePath.toUri());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        if (!recurso.exists() && !recurso.isReadable()) {
            filePath = Paths.get("src/main/resources/static/images").resolve("user-icon.png").toAbsolutePath();

            try {
                recurso = new UrlResource(filePath.toUri());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"");

        return new ResponseEntity<Resource>(recurso, header, HttpStatus.OK);
    }

    @DeleteMapping("/doctors/{id}")
    public ResponseEntity<?> deletePatient(@PathVariable Long id){
        Map<String, Object> response = new HashMap<>();

        try{
            User patient = patientService.getUser(id);

            if(patient != null){
                String previousNamePhoto = patient.getPhoto();
                if(previousNamePhoto != null && previousNamePhoto.length() > 0){
                    Path previousPhotoRoute = Paths.get("uploads").resolve(previousNamePhoto).toAbsolutePath();
                    File previousPhotoFile = previousPhotoRoute.toFile();

                    if(previousPhotoFile.exists() && previousPhotoFile.canRead()){
                        previousPhotoFile.delete();
                    }
                }

                patientService.deletePatient(id);
                response.put("message", "Se ha eliminado al paciente con éxito.");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
            }

            response.put("message", "El paciente no existe.");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }catch(Exception e){
            response.put("message", "Error al eliminar al paciente");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
