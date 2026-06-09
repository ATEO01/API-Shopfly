package com.ecommerce.service;

import com.ecommerce.model.Notification;
import com.ecommerce.model.Utilisateur;
import com.ecommerce.repository.NotificationRepository;
import com.ecommerce.repository.UtilisateurRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ObjectMapper objectMapper;
    
    public NotificationService(NotificationRepository notificationRepository,
                               UtilisateurRepository utilisateurRepository) {
        this.notificationRepository = notificationRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.objectMapper = new ObjectMapper();
    }
    
    // Créer une notification pour un utilisateur
    public void creerNotification(Utilisateur utilisateur, String titre, String message, String type, Map<String, Object> data) {
        Notification notification = new Notification();
        notification.setUtilisateur(utilisateur);
        notification.setTitre(titre);
        notification.setMessage(message);
        notification.setType(type);
        
        if (data != null) {
            try {
                notification.setData(objectMapper.writeValueAsString(data));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                notification.setData("{}");
            }
        }
        
        notificationRepository.save(notification);
    }
    
    // Créer une notification pour tous les clients
    public void notifierTousLesClients(String titre, String message, String type, Map<String, Object> data) {
        List<Utilisateur> clients = utilisateurRepository.findByRole("CLIENT");
        for (Utilisateur client : clients) {
            creerNotification(client, titre, message, type, data);
        }
    }
    
    // Créer une notification pour tous les admins
    public void notifierTousLesAdmins(String titre, String message, String type, Map<String, Object> data) {
        List<Utilisateur> admins = utilisateurRepository.findByRoleIn(List.of("ADMIN", "ADMIN_PRINCIPAL"));
        for (Utilisateur admin : admins) {
            creerNotification(admin, titre, message, type, data);
        }
    }
    
    // Récupérer les notifications d'un utilisateur
    public List<Notification> getNotifications(Utilisateur utilisateur) {
        return notificationRepository.findByUtilisateurOrderByDateCreationDesc(utilisateur);
    }
    
    // Compter les notifications non lues
    public long countNonLues(Utilisateur utilisateur) {
        return notificationRepository.countByUtilisateurAndEstLu(utilisateur, false);
    }
    
    // Marquer tout comme lu
    @Transactional
    public void marquerToutCommeLu(Utilisateur utilisateur) {
        notificationRepository.marquerToutCommeLu(utilisateur);
    }
    
    // Supprimer une notification
    @Transactional
    public void supprimerNotification(Long notificationId, Utilisateur utilisateur) {
        notificationRepository.deleteById(notificationId);
    }
    
    // Supprimer toutes les notifications d'un utilisateur
    @Transactional
    public void supprimerToutesNotifications(Utilisateur utilisateur) {
        notificationRepository.deleteByUtilisateur(utilisateur);
    }
}