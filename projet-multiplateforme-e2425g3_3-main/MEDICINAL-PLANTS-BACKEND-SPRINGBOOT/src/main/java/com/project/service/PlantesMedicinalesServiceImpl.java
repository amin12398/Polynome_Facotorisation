package com.project.service;

import com.project.entity.PlantesMedicinales;
import com.project.repository.PlantesMedicinalesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlantesMedicinalesServiceImpl implements PlantesMedicinalesService {

    private final PlantesMedicinalesRepository repository;

    @Autowired
    public PlantesMedicinalesServiceImpl(PlantesMedicinalesRepository repository) {
        this.repository = repository;
    }

    @Override
    public PlantesMedicinales createPlante(PlantesMedicinales plante) {
        return repository.save(plante);
    }

    @Override
    public PlantesMedicinales updatePlante(Long id, PlantesMedicinales plante) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Plante with ID " + id + " does not exist.");
        }
        plante.setId(id); // Set the ID of the existing entity
        return repository.save(plante);
    }

    @Override
    public PlantesMedicinales getPlanteById(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Plante with ID " + id + " not found."));
    }

    @Override
    public List<PlantesMedicinales> getAllPlantes() {
        return repository.findAll();
    }

    @Override
    public void deletePlante(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Plante with ID " + id + " does not exist.");
        }
        repository.deleteById(id);
    }

    @Override
    public List<PlantesMedicinales> searchPlantes(String nom, String regionGeo, String proprietes, String utilisation) {
        return repository.findByNomContainingIgnoreCaseOrRegionGeoContainingIgnoreCaseOrProprietesContainingIgnoreCaseOrUtilisationContainingIgnoreCase(
                nom, regionGeo, proprietes, utilisation);
    }

    @Override
    public List<PlantesMedicinales> recommendPlantes(String description, String utilisation, String precautions, String interactions, String proprietes) {
        // Search plants based on multiple fields, matching keywords provided by the user
        return repository.findByDescriptionContainingIgnoreCaseOrUtilisationContainingIgnoreCaseOrPrecautionsContainingIgnoreCaseOrInteractionsContainingIgnoreCaseOrProprietesContainingIgnoreCase(
                description, utilisation, precautions, interactions, proprietes);
    }
}
