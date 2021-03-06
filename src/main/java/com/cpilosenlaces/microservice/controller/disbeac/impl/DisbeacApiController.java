package com.cpilosenlaces.microservice.controller.disbeac.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.validation.ConstraintViolationException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.cpilosenlaces.microservice.controller.disbeac.DisbeacApi;
import com.cpilosenlaces.microservice.exception.BadRequestException;
import com.cpilosenlaces.microservice.exception.ErrorResponse;
import com.cpilosenlaces.microservice.exception.NotFoundException;
import com.cpilosenlaces.microservice.model.disbeac.Disbeac;
import com.cpilosenlaces.microservice.model.disbeac.dto.DisbeacDTO;
import com.cpilosenlaces.microservice.model.util.HandledResponse;
import com.cpilosenlaces.microservice.service.disbeac.DisbeacService;

@Controller
public class DisbeacApiController implements DisbeacApi {

    @Autowired
    private DisbeacService ds;

    @Override
    public ResponseEntity<Disbeac> getById(UUID id) throws NotFoundException {
        try {
            return new ResponseEntity<>(ds.findById(id), HttpStatus.OK);
        } catch (NotFoundException nfe) {
            throw new NotFoundException("Disbeac with ID " + id + " does not exists.");
        }
    }

    @Override
    public ResponseEntity<List<Disbeac>> getByUserId(UUID userId) {
        return new ResponseEntity<>(ds.findByUserId(userId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Disbeac> getByMac(String mac) {
        List<Disbeac> disbeac = ds.findByMac(mac);
        return new ResponseEntity<>(disbeac.get(0), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Disbeac> save(DisbeacDTO disbeacDTO) throws NotFoundException {
        List<Disbeac> disbeacs = ds.findByMac(disbeacDTO.getMac());
        
        if (disbeacs.size() > 0) {
            Disbeac disbeac = disbeacs.get(0);
            if (disbeac.getUserId() == disbeacDTO.getUserId()) {
                return new ResponseEntity<>(disbeac, HttpStatus.OK);
            } else {
                disbeac.setUserId(disbeacDTO.getUserId());
                ds.updateUserId(disbeacDTO.getUserId(), disbeac.getId());

                return new ResponseEntity<>(disbeac, HttpStatus.OK);
            }
        }

        ModelMapper mapper = new ModelMapper();
        Disbeac disbeac = mapper.map(disbeacDTO, Disbeac.class);
        disbeac.setId(UUID.randomUUID());

        return new ResponseEntity<>(ds.save(disbeac), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<HandledResponse> update(UUID id, DisbeacDTO disbeacDTO) throws NotFoundException {

        Disbeac disbeac = null;
        try {
            disbeac = ds.findById(id);
        } catch (NotFoundException nfe) {
            throw new NotFoundException("Disbeac with ID " + disbeacDTO.getUserId() + " does not exists.");
        }

        ModelMapper mapper = new ModelMapper();
        disbeac = mapper.map(disbeacDTO, Disbeac.class);
        disbeac.setDate(System.currentTimeMillis());

        ds.save(disbeac);

        return new ResponseEntity<>(new HandledResponse("Disbeac updated", 1), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<HandledResponse> updateUserId(UUID id, UUID userId) throws NotFoundException {
        try {
            ds.findById(id);
        } catch (NotFoundException nfe) {
            throw new NotFoundException("Disbeac with ID " + id + " does not exists");
        }

        ds.updateUserId(userId, id);

        return new ResponseEntity<>(new HandledResponse("Disbeac's user updated", 1), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Disbeac> delete(UUID id) throws NotFoundException {
        try {
            Disbeac disbeac = ds.findById(id);
            ds.delete(disbeac);

            return new ResponseEntity<>(disbeac, HttpStatus.OK);
        } catch (NotFoundException nfe) {
            throw new NotFoundException("Disbeac with ID " + id + " does not exists.");
        }
    }

    @Override
    public ResponseEntity<List<Disbeac>> deleteByUserId(UUID userId) {
        List<Disbeac> disbeacs = ds.findByUserId(userId);
        ds.deleteByUser(disbeacs);

        return new ResponseEntity<>(disbeacs, HttpStatus.OK);
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
