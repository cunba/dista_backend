package com.cpilosenlaces.microservice.service;

import java.util.List;
import java.util.UUID;

import com.cpilosenlaces.microservice.exception.NotFoundException;
import com.cpilosenlaces.microservice.model.Pressure;

public interface PressureService {
    List<Pressure> findByDisbandId(UUID disbandId);

    List<Pressure> findByDateBetweenAndDisbandId(long minDate, long maxDate, UUID disbandId);

    List<Pressure> findByDateBetween(long minDate, long maxDate);

    Pressure findById(UUID id) throws NotFoundException;

    List<Pressure> findAll();

    Pressure save(Pressure pressure);

    void deleteByDisband(List<Pressure> listPressure);
}