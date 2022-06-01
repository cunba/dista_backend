package com.cpilosenlaces.disheap_backend.service.impl;

import java.util.List;
import java.util.UUID;

import com.cpilosenlaces.disheap_backend.exception.NotFoundException;
import com.cpilosenlaces.disheap_backend.model.Lightning;
import com.cpilosenlaces.disheap_backend.repository.LightningRepository;
import com.cpilosenlaces.disheap_backend.service.LightningService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LightningServiceImpl implements LightningService {

    @Autowired
    private LightningRepository lr;

    @Override
    public List<Lightning> findByDisbandId(UUID disbandId) {
        return lr.findByDisbandId(disbandId);
    }

    @Override
    public List<Lightning> findByDateBetween(long minDate, long maxDate) {
        return lr.findByDateBetween(minDate, maxDate);
    }

    @Override
    public List<Lightning> findByDateBetweenAndDisbandId(long minDate, long maxDate, UUID disbandId) {
        return lr.findByDateBetweenAndDisbandId(minDate, maxDate, disbandId);
    }

    @Override
    public Lightning findById(UUID id) throws NotFoundException {
        return lr.findById(id).orElseThrow(NotFoundException::new);
    }

    @Override
    public List<Lightning> findAll() {
        return lr.findAll();
    }

    @Override
    public Lightning save(Lightning lightning) {
        return lr.save(lightning);
    }

    @Override
    public void deleteByDisband(List<Lightning> listLightning) {
        lr.deleteAll(listLightning);
    }

}
