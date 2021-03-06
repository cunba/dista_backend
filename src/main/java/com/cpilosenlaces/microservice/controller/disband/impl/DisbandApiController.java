package com.cpilosenlaces.microservice.controller.disband.impl;

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

import com.cpilosenlaces.microservice.controller.disband.DisbandApi;
import com.cpilosenlaces.microservice.exception.BadRequestException;
import com.cpilosenlaces.microservice.exception.ErrorResponse;
import com.cpilosenlaces.microservice.exception.NotFoundException;
import com.cpilosenlaces.microservice.model.disband.Disband;
import com.cpilosenlaces.microservice.model.disband.dto.DisbandDTO;
import com.cpilosenlaces.microservice.model.util.HandledResponse;
import com.cpilosenlaces.microservice.service.disband.DisbandService;

@Controller
public class DisbandApiController implements DisbandApi {

    @Autowired
    private DisbandService ds;

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
    public ResponseEntity<Disband> getByMac(String mac) {
        List<Disband> disband = ds.findByMac(mac);
        return new ResponseEntity<>(disband.get(0), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Disband> save(DisbandDTO disbandDTO) throws NotFoundException {
        
        List<Disband> disbands = ds.findByMac(disbandDTO.getMac());
        
        if (disbands.size() > 0) {
            Disband disband = disbands.get(0);
            if (disband.getUserId() == disbandDTO.getUserId()) {
                return new ResponseEntity<>(disband, HttpStatus.OK);
            } else {
                disband.setUserId(disbandDTO.getUserId());
                ds.updateUserId(disbandDTO.getUserId(), disband.getId());

                return new ResponseEntity<>(disband, HttpStatus.OK);
            }
        }

        ModelMapper mapper = new ModelMapper();
        Disband disband = mapper.map(disbandDTO, Disband.class);
        disband.setId(UUID.randomUUID());

        return new ResponseEntity<>(ds.save(disband), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<HandledResponse> update(UUID id, DisbandDTO disbandDTO) throws NotFoundException {
        Disband disband = null;
        try {
            disband = ds.findById(id);
        } catch (NotFoundException nfe) {
            throw new NotFoundException("Disband with ID " + disbandDTO.getUserId() + " does not exists.");
        }

        ModelMapper mapper = new ModelMapper();
        disband = mapper.map(disbandDTO, Disband.class);
        disband.setDate(System.currentTimeMillis());

        ds.save(disband);

        return new ResponseEntity<>(new HandledResponse("Disband updated", 1), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<HandledResponse> updateUserId(UUID id, UUID userId) throws NotFoundException {
        try {
            ds.findById(id);
        } catch (NotFoundException nfe) {
            throw new NotFoundException("Disband with ID " + id + " does not exists");
        }

        ds.updateUserId(userId, id);

        return new ResponseEntity<>(new HandledResponse("Disband's user updated", 1), HttpStatus.OK);
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
        ds.deleteByUser(disbands);

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
