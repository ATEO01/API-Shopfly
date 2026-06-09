package com.ecommerce.repository;

import com.ecommerce.model.Livraison;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LivraisonRepository extends JpaRepository<Livraison, Long> {
    List<Livraison> findAllByOrderByDateCreationDesc();
    List<Livraison> findByStatut(String statut);
}