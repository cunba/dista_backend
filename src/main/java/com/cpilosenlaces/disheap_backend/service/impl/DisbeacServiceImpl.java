package com.cpilosenlaces.disheap_backend.service.impl;

import java.util.List;
import java.util.UUID;

import com.cpilosenlaces.disheap_backend.exception.NotFoundException;
import com.cpilosenlaces.disheap_backend.model.Disbeac;
import com.cpilosenlaces.disheap_backend.repository.DisbeacRepository;
import com.cpilosenlaces.disheap_backend.service.DisbeacService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DisbeacServiceImpl implements DisbeacService {

    @Autowired
    private DisbeacRepository dr;

    @Override
    public List<Disbeac> findByUserId(UUID userId) {
        return dr.findByUserId(userId);
    }

    @Override
    public Disbeac findById(UUID id) throws NotFoundException {
        return dr.findById(id).orElseThrow(NotFoundException::new);
    }

    @Override
    public List<Disbeac> findAll() {
        return dr.findAll();
    }

    @Override
    public Disbeac save(Disbeac disbeac) {
        return dr.save(disbeac);
    }

    @Override
    public void delete(Disbeac disbeac) {
        dr.delete(disbeac);
    }

    @Override
    public void deleteAll() {
        dr.deleteAll();
    }

}
