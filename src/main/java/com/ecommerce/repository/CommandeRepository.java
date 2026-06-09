package com.ecommerce.repository;

import com.ecommerce.model.Commande;
import com.ecommerce.model.Utilisateur;
import com.ecommerce.model.Livraison;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommandeRepository extends JpaRepository<Commande, Long> {
    List<Commande> findByUtilisateurOrderByDateCommandeDesc(Utilisateur utilisateur);
    List<Commande> findAllByOrderByDateCommandeDesc();
    List<Commande> findByStatutAndLivraisonIsNull(String statut);
    List<Commande> findByLivraison(Livraison livraison);
}