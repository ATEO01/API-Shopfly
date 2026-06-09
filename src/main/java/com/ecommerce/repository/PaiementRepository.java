package com.ecommerce.repository;

import com.ecommerce.model.Paiement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PaiementRepository extends JpaRepository<Paiement, Long> {
    Optional<Paiement> findByReferencePaiement(String referencePaiement);
    Optional<Paiement> findByCommandeId(Long commandeId);
}