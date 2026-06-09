package com.ecommerce.repository;

import com.ecommerce.model.Panier;
import com.ecommerce.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PanierRepository extends JpaRepository<Panier, Long> {
    Optional<Panier> findByUtilisateur(Utilisateur utilisateur);
}