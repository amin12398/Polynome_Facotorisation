package com.project.service;

import com.project.entity.PlantesMedicinales;

import java.util.List;

public interface PlantesMedicinalesService {
    PlantesMedicinales createPlante(PlantesMedicinales plante);
    PlantesMedicinales updatePlante(Long id, PlantesMedicinales plante);
    PlantesMedicinales getPlanteById(Long id);
    List<PlantesMedicinales> getAllPlantes();
    void deletePlante(Long id);

    // Existing method for searching plants
    List<PlantesMedicinales> searchPlantes(String nom, String regionGeo, String proprietes, String utilisation);

    // New method for personalized recommendations
    List<PlantesMedicinales> recommendPlantes(String description, String utilisation, String precautions, String interactions, String proprietes);
}
