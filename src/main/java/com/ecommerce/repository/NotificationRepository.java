package com.ecommerce.repository;

import com.ecommerce.model.Notification;
import com.ecommerce.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUtilisateurOrderByDateCreationDesc(Utilisateur utilisateur);
    List<Notification> findByUtilisateurAndEstLuOrderByDateCreationDesc(Utilisateur utilisateur, boolean estLu);
    long countByUtilisateurAndEstLu(Utilisateur utilisateur, boolean estLu);
    
    @Transactional
    @Modifying
    @Query("UPDATE Notification n SET n.estLu = true WHERE n.utilisateur = :utilisateur")
    void marquerToutCommeLu(Utilisateur utilisateur);
    
    @Transactional
    void deleteByUtilisateur(Utilisateur utilisateur);
}