package com.cpilosenlaces.microservice.controller.disband.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.cpilosenlaces.microservice.controller.disband.TemperatureApi;
import com.cpilosenlaces.microservice.exception.BadRequestException;
import com.cpilosenlaces.microservice.exception.ErrorResponse;
import com.cpilosenlaces.microservice.exception.NotFoundException;
import com.cpilosenlaces.microservice.model.disband.Disband;
import com.cpilosenlaces.microservice.model.disband.MeasureResponse;
import com.cpilosenlaces.microservice.model.disband.Temperature;
import com.cpilosenlaces.microservice.model.disband.dto.MeasureDTO;
import com.cpilosenlaces.microservice.service.disband.DisbandService;
import com.cpilosenlaces.microservice.service.disband.TemperatureService;

@Controller
public class TemperatureApiController implements TemperatureApi {

    @Autowired
    private TemperatureService ts;
    @Autowired
    private DisbandService ds;

    @Override
    public ResponseEntity<Temperature> getLast1ByDateBetweenAndDisbandId(long minDate, long maxDate, UUID disbandId) {
        return new ResponseEntity<>(ts.findLast1ByDateBetweenAndDisbandId(minDate, maxDate, disbandId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<Temperature>> getAll() {
        return new ResponseEntity<>(ts.findAll(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Temperature> getById(UUID id) throws NotFoundException {
        try {
            return new ResponseEntity<>(ts.findById(id), HttpStatus.OK);
        } catch (NotFoundException nfe) {
            throw new NotFoundException("Temperature with ID " + id + " does not exists.");
        }
    }

    @Override
    public ResponseEntity<List<Temperature>> getByDisbandId(UUID disbandId) {
        return new ResponseEntity<>(ts.findByDisbandId(disbandId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<MeasureResponse<Temperature>> getByDateBetweenAndDisbandId(long minDate, long maxDate,
            UUID disbandId) {

        long changerDate = System.currentTimeMillis();
        if (minDate > maxDate) {
            changerDate = minDate;
            minDate = maxDate;
            maxDate = changerDate;
        }

        List<Temperature> list = ts.findByDateBetweenAndDisbandId(minDate, maxDate, disbandId);
        Temperature minMeasure = ts.findMinValueByDateBetweenAndDisbandId(minDate, maxDate, disbandId);
        Temperature maxMeasure = ts.findMaxValueByDateBetweenAndDisbandId(minDate, maxDate, disbandId);

        return new ResponseEntity<>(new MeasureResponse<Temperature>(list, minMeasure.getData(), maxMeasure.getData()),
                HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<Temperature>> getByDateBetween(long minDate, long maxDate) {

        long changerDate = System.currentTimeMillis();
        if (minDate > maxDate) {
            changerDate = minDate;
            minDate = maxDate;
            maxDate = changerDate;
        }

        return new ResponseEntity<>(ts.findByDateBetween(minDate, maxDate), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Temperature> save(MeasureDTO measureDTO) throws NotFoundException, BadRequestException {

        Disband disband = null;
        List<Disband> disbands = ds.findByMac(measureDTO.getDisbandMac());
        if (disbands.size() > 0) {
            disband = disbands.get(0);
        } else {
            throw new NotFoundException("Disband with MAC " + measureDTO.getDisbandMac() + " does not exists.");
        }

        Temperature temperature = new Temperature();

        temperature.setId(UUID.randomUUID());
        temperature.setData(measureDTO.getData());
        temperature.setDate(measureDTO.getDate());
        temperature.setDisband(disband);

        return new ResponseEntity<>(ts.save(temperature), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<List<Temperature>> deleteByDisbandId(UUID disbandId) throws NotFoundException {
        try {
            ds.findById(disbandId);
        } catch (NotFoundException nfe) {
            throw new NotFoundException("Disband with ID " + disbandId + " does not exists.");
        }

        List<Temperature> listTemperatures = ts.findByDisbandId(disbandId);
        ts.deleteByDisband(listTemperatures);

        return new ResponseEntity<>(listTemperatures, HttpStatus.OK);
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
