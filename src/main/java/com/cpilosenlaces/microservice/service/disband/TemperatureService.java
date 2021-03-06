package com.cpilosenlaces.microservice.service.disband;

import java.util.List;
import java.util.UUID;

import com.cpilosenlaces.microservice.exception.NotFoundException;
import com.cpilosenlaces.microservice.model.disband.Temperature;

public interface TemperatureService {
    Temperature findLast1ByDateBetweenAndDisbandId(long minDate, long maxDate, UUID disbandId);
    
    Temperature findMaxValueByDateBetweenAndDisbandId(long minDate, long maxDate, UUID disbandId);
    
    Temperature findMinValueByDateBetweenAndDisbandId(long minDate, long maxDate, UUID disbandId);

    List<Temperature> findByDisbandId(UUID disbandId);

    List<Temperature> findByDateBetweenAndDisbandId(long minDate, long maxDate, UUID disbandId);

    List<Temperature> findByDateBetween(long minDate, long maxDate);

    Temperature findById(UUID id) throws NotFoundException;

    List<Temperature> findAll();

    Temperature save(Temperature temperature);

    void deleteByDisband(List<Temperature> listTemperature);
}
