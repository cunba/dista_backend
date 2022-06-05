package com.cpilosenlaces.microservice.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cpilosenlaces.microservice.exception.NotFoundException;
import com.cpilosenlaces.microservice.model.Oxygen;
import com.cpilosenlaces.microservice.repository.OxygenRepository;
import com.cpilosenlaces.microservice.service.OxygenService;

@Service
public class OxygenServiceImpl implements OxygenService {

    @Autowired
    private OxygenRepository or;

    @Override
    public List<Oxygen> findByDisbandId(UUID disbandId) {
        return or.findByDisbandId(disbandId);
    }

    @Override
    public List<Oxygen> findByDateBetween(long minDate, long maxDate) {
        return or.findByDateBetween(minDate, maxDate);
    }

    @Override
    public List<Oxygen> findByDateBetweenAndDisbandId(long minDate, long maxDate, UUID disbandId) {
        return or.findByDateBetweenAndDisbandId(minDate, maxDate, disbandId);
    }

    @Override
    public Oxygen findById(UUID id) throws NotFoundException {
        return or.findById(id).orElseThrow(NotFoundException::new);
    }

    @Override
    public List<Oxygen> findAll() {
        return or.findAll();
    }

    @Override
    public Oxygen save(Oxygen oxygen) {
        return or.save(oxygen);
    }

    @Override
    public void deleteByDisband(List<Oxygen> listOxygen) {
        or.deleteAll(listOxygen);
    }

}