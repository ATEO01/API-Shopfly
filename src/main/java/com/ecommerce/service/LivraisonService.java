package com.ecommerce.service;

import com.ecommerce.dto.LivraisonRequest;
import com.ecommerce.dto.LivraisonResponse;
import com.ecommerce.model.Commande;
import com.ecommerce.model.Livraison;
import com.ecommerce.repository.CommandeRepository;
import com.ecommerce.repository.LivraisonRepository;
import com.ecommerce.service.NotificationService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LivraisonService {
    
    private final LivraisonRepository livraisonRepository;
    private final CommandeRepository commandeRepository;
    private final NotificationService notificationService;
    
    // Récupérer toutes les livraisons
    public List<LivraisonResponse> getAllLivraisons() {
        return livraisonRepository.findAllByOrderByDateCreationDesc().stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
    
    // Récupérer les commandes disponibles (PAYEE et sans livraison)
    public List<LivraisonResponse.CommandeLivraisonDto> getCommandesDisponibles() {
        return commandeRepository.findByStatutAndLivraisonIsNull("PRET").stream()
            .map(this::toCommandeDto)
            .collect(Collectors.toList());
    }
    
    // Créer une livraison
    @Transactional
    public LivraisonResponse creerLivraison(LivraisonRequest request) {
        // Générer la référence
        String reference = "LIV-" + System.currentTimeMillis();
        
        Livraison livraison = new Livraison();
        livraison.setReference(reference);
        livraison.setStatut("PAS_TERMINEE");
        
        // Ajouter les commandes
        List<Commande> commandes = commandeRepository.findAllById(request.getCommandesIds());
        for (Commande commande : commandes) {
            commande.setLivraison(livraison);
            commande.setStatut(Commande.STATUT_EN_COURS_DE_LIVRAISON);
        }
        
        livraison.setCommandes(commandes);
        livraisonRepository.save(livraison);

    // NOTIFICATION aux clients concernés
    for (Commande commande : commandes) {
        Map<String, Object> data = new HashMap<>();
        data.put("commandeId", commande.getId());
        data.put("livraisonId", livraison.getId());
        
        notificationService.creerNotification(
            commande.getUtilisateur(),
            "🚚 En cours de livraison",
            "Votre commande #" + commande.getId() + " est en cours de livraison",
            "LIVRAISON_EN_COURS",
            data
        );
    }
    
    // NOTIFICATION aux admins
    notificationService.notifierTousLesAdmins(
        "📦 Nouvelle livraison",
        "Livraison #" + livraison.getId() + " en cours",
        "NOUVELLE_LIVRAISON",
        Map.of("livraisonId", livraison.getId())
    );

        return toResponse(livraison);
    }
    
    // Changer le statut d'une livraison
    @Transactional
    public LivraisonResponse updateStatut(Long livraisonId, String statut) {   
        Livraison livraison = livraisonRepository.findById(livraisonId)
            .orElseThrow(() -> new RuntimeException("Livraison non trouvée"));
    
        livraison.setStatut(statut);
    
        if ("TERMINEE".equals(statut)) {
            livraison.setDateTerminaison(LocalDateTime.now());
        
            // Mettre à jour le statut des commandes à LIVRE
            for (Commande commande : livraison.getCommandes()) {
                commande.setStatut(Commande.STATUT_LIVRE);
                commandeRepository.save(commande);
            }
        }
    
        livraisonRepository.save(livraison);
            if ("TERMINEE".equals(statut)) {
        // NOTIFICATION aux clients
        for (Commande commande : livraison.getCommandes()) {
            Map<String, Object> data = new HashMap<>();
            data.put("commandeId", commande.getId());
            
            notificationService.creerNotification(
                commande.getUtilisateur(),
                "✅ Commande livrée",
                "Votre commande #" + commande.getId() + " a été livrée avec succès",
                "COMMANDE_LIVREE",
                data
            );
        }
        
        // NOTIFICATION aux admins
        notificationService.notifierTousLesAdmins(
            "✅ Livraison terminée",
            "La livraison #" + livraisonId + " a été effectuée avec succès",
            "LIVRAISON_TERMINEE",
            Map.of("livraisonId", livraisonId)
        );
    }
        return toResponse(livraison);
    }
    
    private LivraisonResponse toResponse(Livraison livraison) {
        return LivraisonResponse.builder()
            .id(livraison.getId())
            .reference(livraison.getReference())
            .statut(livraison.getStatut())
            .dateCreation(livraison.getDateCreation())
            .dateTerminaison(livraison.getDateTerminaison())
            .commandes(livraison.getCommandes().stream()
                .map(this::toCommandeDto)
                .collect(Collectors.toList()))
            .success(true)
            .build();
    }
    
    private LivraisonResponse.CommandeLivraisonDto toCommandeDto(Commande commande) {
        return LivraisonResponse.CommandeLivraisonDto.builder()
            .id(commande.getId())
            .reference("CMD-" + commande.getId())
            .montantTotal(commande.getMontantTotal())
            .clientNom(commande.getUtilisateur().getPrenom() + " " + commande.getUtilisateur().getNom())
            .clientEmail(commande.getUtilisateur().getEmail())
            .adresseLivraison(commande.getAdresseLivraison())
            .build();
    }
}
