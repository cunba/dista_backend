package com.cpilosenlaces.disheap_backend.controller.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.validation.ConstraintViolationException;

import com.cpilosenlaces.disheap_backend.controller.DisbandApi;
import com.cpilosenlaces.disheap_backend.exception.BadRequestException;
import com.cpilosenlaces.disheap_backend.exception.ErrorResponse;
import com.cpilosenlaces.disheap_backend.exception.NotFoundException;
import com.cpilosenlaces.disheap_backend.model.Disband;
import com.cpilosenlaces.disheap_backend.model.UserModel;
import com.cpilosenlaces.disheap_backend.model.dto.DisbandDTO;
import com.cpilosenlaces.disheap_backend.service.DisbandService;
import com.cpilosenlaces.disheap_backend.service.UserService;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Controller
public class DisbandApiController implements DisbandApi {

    @Autowired
    private DisbandService ds;
    @Autowired
    private UserService us;

    @Override
    public ResponseEntity<Disband> getById(UUID id) throws NotFoundException {
        try {
            return new ResponseEntity<>(ds.findById(id), HttpStatus.OK);
        } catch (NotFoundException nfe) {
            throw new NotFoundException("Disband with ID " + id + " does not exists.");
        }
    }

    @Override
    public ResponseEntity<List<Disband>> getByUserId(UUID userId) {
        return new ResponseEntity<>(ds.findByUserId(userId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<Disband>> getByMac(String mac) {
        return new ResponseEntity<>(ds.findByMac(mac), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Disband> save(DisbandDTO disbandDTO) throws NotFoundException {
        UserModel user = null;
        try {
            user = us.findById(disbandDTO.getUserId());
        } catch (NotFoundException nfe) {
            throw new NotFoundException("User with ID " + disbandDTO.getUserId() + " does not exists.");
        }

        ModelMapper mapper = new ModelMapper();
        Disband disband = mapper.map(disbandDTO, Disband.class);
        disband.setId(UUID.randomUUID());
        disband.setDate(LocalDateTime.now());
        disband.setUser(user);

        return new ResponseEntity<>(ds.save(disband), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<String> update(UUID id, DisbandDTO disbandDTO) throws NotFoundException {
        Disband disband = null;
        try {
            disband = ds.findById(id);
        } catch (NotFoundException nfe) {
            throw new NotFoundException("Disband with ID " + disbandDTO.getUserId() + " does not exists.");
        }

        UserModel user = null;
        try {
            user = us.findById(disbandDTO.getUserId());
        } catch (NotFoundException nfe) {
            throw new NotFoundException("User with ID " + disbandDTO.getUserId() + " does not exists.");
        }

        ModelMapper mapper = new ModelMapper();
        disband = mapper.map(disbandDTO, Disband.class);
        disband.setDate(LocalDateTime.now());
        disband.setUser(user);

        ds.save(disband);

        return new ResponseEntity<>("Disband updated.", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Disband> delete(UUID id) throws NotFoundException {
        try {
            Disband disband = ds.findById(id);
            ds.delete(disband);

            return new ResponseEntity<>(disband, HttpStatus.OK);
        } catch (NotFoundException nfe) {
            throw new NotFoundException("Disband with ID " + id + " does not exists.");
        }
    }

    @Override
    public ResponseEntity<List<Disband>> deleteByUserId(UUID userId) {
        List<Disband> disbands = ds.findByUserId(userId);

        for (Disband disband : disbands) {
            ds.delete(disband);
        }

        return new ResponseEntity<>(disbands, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<Disband>> deleteAll() {
        List<Disband> disbands = ds.findAll();
        ds.deleteAll();
        return new ResponseEntity<>(disbands, HttpStatus.OK);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException br) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Bad request exception");
        ErrorResponse errorResponse = new ErrorResponse("400", error, br.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException nfe) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Not Found Exception");
        ErrorResponse errorResponse = new ErrorResponse("404", error, nfe.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleArgumentNotValidException(MethodArgumentNotValidException manve) {
        Map<String, String> errors = new HashMap<>();
        manve.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException cve) {
        Map<String, String> errors = new HashMap<>();
        cve.getConstraintViolations().forEach(error -> {
            String fieldName = error.getPropertyPath().toString();
            String message = error.getMessage();
            errors.put(fieldName, message);
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(Exception exception) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Internal server error");
        ErrorResponse errorResponse = new ErrorResponse("500", error, exception.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
