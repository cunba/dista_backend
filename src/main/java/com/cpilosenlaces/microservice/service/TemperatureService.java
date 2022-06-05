package com.cpilosenlaces.microservice.service;

import java.util.List;
import java.util.UUID;

import com.cpilosenlaces.microservice.exception.NotFoundException;
import com.cpilosenlaces.microservice.model.Temperature;

public interface TemperatureService {
    List<Temperature> findByDisbandId(UUID disbandId);

    List<Temperature> findByDateBetweenAndDisbandId(long minDate, long maxDate, UUID disbandId);

    List<Temperature> findByDateBetween(long minDate, long maxDate);

    Temperature findById(UUID id) throws NotFoundException;

    List<Temperature> findAll();

    Temperature save(Temperature temperature);

    void deleteByDisband(List<Temperature> listTemperature);
}