package com.ecommerce.controller.client;

import com.ecommerce.model.Commande;
import com.ecommerce.model.CommandeItem;
import com.ecommerce.model.Paiement;
import com.ecommerce.model.Panier;
import com.ecommerce.model.Produit;
import com.ecommerce.model.Utilisateur;
import com.ecommerce.repository.CommandeRepository;
import com.ecommerce.repository.PaiementRepository;
import com.ecommerce.repository.PanierRepository;
import com.ecommerce.repository.ProduitRepository;
import com.ecommerce.repository.UtilisateurRepository;
import com.ecommerce.service.FirebaseAuthService;
import com.ecommerce.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/client/commandes")
public class CommandeClientController {
    
    private final CommandeRepository commandeRepository;
    private final PaiementRepository paiementRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PanierRepository panierRepository;
    private final ProduitRepository produitRepository;
    private final FirebaseAuthService firebaseAuthService;
    private final NotificationService notificationService;
    
    public CommandeClientController(CommandeRepository commandeRepository,
                                    PaiementRepository paiementRepository,
                                    UtilisateurRepository utilisateurRepository,
                                    PanierRepository panierRepository,
                                    ProduitRepository produitRepository,
                                    FirebaseAuthService firebaseAuthService,
                                    NotificationService notificationService) {
        this.commandeRepository = commandeRepository;
        this.paiementRepository = paiementRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.panierRepository = panierRepository;
        this.produitRepository = produitRepository;
        this.firebaseAuthService = firebaseAuthService;
        this.notificationService = notificationService;
    }
    
    private Utilisateur getUtilisateur(String token) {
        try {
            String uid = firebaseAuthService.verifierToken(token.substring(7)).getUid();
            return utilisateurRepository.findByFirebaseUid(uid).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }
    
    // Récupérer mes commandes
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getMesCommandes(@RequestHeader("Authorization") String token) {
        Utilisateur utilisateur = getUtilisateur(token);
        if (utilisateur == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        
        List<Commande> commandes = commandeRepository.findByUtilisateurOrderByDateCommandeDesc(utilisateur);
        List<Map<String, Object>> resultats = new ArrayList<>();
        
        for (Commande commande : commandes) {
            Map<String, Object> cmd = new HashMap<>();
            cmd.put("id", commande.getId());
            cmd.put("montantTotal", commande.getMontantTotal());
            cmd.put("statut", commande.getStatut());
            cmd.put("dateCommande", commande.getDateCommande().toString());
            cmd.put("adresseLivraison", commande.getAdresseLivraison());
            
            List<Map<String, Object>> items = new ArrayList<>();
            for (CommandeItem item : commande.getItems()) {
                Map<String, Object> it = new HashMap<>();
                it.put("id", item.getId());
                it.put("produitNom", item.getProduitNom());
                it.put("prix", item.getPrix());
                it.put("quantite", item.getQuantite());
                it.put("sousTotal", item.getSousTotal());
                items.add(it);
            }
            cmd.put("items", items);
            resultats.add(cmd);
        }
        
        return ResponseEntity.ok(resultats);
    }
    
    // Créer une commande
    @PostMapping("/creer")
    public ResponseEntity<Map<String, Object>> creerCommande(@RequestHeader("Authorization") String token,
                                                              @RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        Utilisateur utilisateur = getUtilisateur(token);
        if (utilisateur == null) {
            response.put("success", false);
            response.put("message", "Utilisateur non trouvé");
            return ResponseEntity.badRequest().body(response);
        }
        
        String adresseLivraison = (String) request.get("adresseLivraison");
        Double montantTotal = ((Number) request.get("montantTotal")).doubleValue();
        List<Map<String, Object>> items = (List<Map<String, Object>>) request.get("items");
        
        Commande commande = new Commande();
        commande.setUtilisateur(utilisateur);
        commande.setAdresseLivraison(adresseLivraison);
        commande.setMontantTotal(montantTotal);
        commande.setStatut("EN_ATTENTE");
        
        for (Map<String, Object> item : items) {
            CommandeItem commandeItem = new CommandeItem();
            commandeItem.setCommande(commande);
            
            Long produitId = ((Number) item.get("produitId")).longValue();
            Integer quantite = (Integer) item.get("quantite");
            
            commandeItem.setProduitId(produitId);
            commandeItem.setProduitNom((String) item.get("produitNom"));
            commandeItem.setPrix(((Number) item.get("prix")).doubleValue());
            commandeItem.setQuantite(quantite);
            commandeItem.setSousTotal(((Number) item.get("sousTotal")).doubleValue());
            commande.getItems().add(commandeItem);
            
            // Déstocker
            Produit produit = produitRepository.findById(produitId).orElse(null);
            if (produit != null) {
                produit.setStock(produit.getStock() - quantite);
                produitRepository.save(produit);
            }
        }
        
        commandeRepository.save(commande);

    //NOTIFICATION au client
    Map<String, Object> data = new HashMap<>();
    data.put("commandeId", commande.getId());
    data.put("nombreArticles", commande.getItems().size());
    data.put("montantTotal", commande.getMontantTotal());
    
    notificationService.creerNotification(
        utilisateur,
        "Commande créée",
        "Vous venez de passer une commande de " + commande.getItems().size() + " article(s).",
        "COMMANDE_CREE",
        data
    );
        
        // Vider le panier
        Panier panier = panierRepository.findByUtilisateur(utilisateur).orElse(null);
        if (panier != null) {
            panier.getItems().clear();
            panierRepository.save(panier);
        }
        
        response.put("success", true);
        response.put("commandeId", commande.getId());
        response.put("message", "Commande créée avec succès");
        
        return ResponseEntity.ok(response);
    }
    
    // Annuler une commande (client)
    @PutMapping("/{id}/annuler")
    public ResponseEntity<Map<String, Object>> annulerCommande(@PathVariable Long id,
                                                                @RequestHeader("Authorization") String token) {
        Map<String, Object> response = new HashMap<>();
        
        Utilisateur utilisateur = getUtilisateur(token);
        if (utilisateur == null) {
            response.put("success", false);
            response.put("message", "Utilisateur non trouvé");
            return ResponseEntity.badRequest().body(response);
        }
        
        Commande commande = commandeRepository.findById(id).orElse(null);
        if (commande == null) {
            response.put("success", false);
            response.put("message", "Commande non trouvée");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (!commande.getUtilisateur().getId().equals(utilisateur.getId())) {
            response.put("success", false);
            response.put("message", "Vous n'êtes pas autorisé");
            return ResponseEntity.status(403).body(response);
        }
        
        if (!"EN_ATTENTE".equals(commande.getStatut())) {
            response.put("success", false);
            response.put("message", "Seules les commandes en attente peuvent être annulées");
            return ResponseEntity.badRequest().body(response);
        }
        
        // Restocker
        for (CommandeItem item : commande.getItems()) {
            if (item.getProduitId() != null) {
                Produit produit = produitRepository.findById(item.getProduitId()).orElse(null);
                if (produit != null) {
                    produit.setStock(produit.getStock() + item.getQuantite());
                    produitRepository.save(produit);
                }
            }
        }
        
        commande.setStatut("ANNULEE");
        commandeRepository.save(commande);
        
        response.put("success", true);
        response.put("message", "Commande annulée avec succès");
        
        return ResponseEntity.ok(response);
    }
    
    // Initialiser le paiement
    @PostMapping("/{id}/initier-paiement")
    public ResponseEntity<Map<String, Object>> initierPaiement(@PathVariable Long id,
                                                                @RequestBody Map<String, String> request,
                                                                @RequestHeader("Authorization") String token) {
        Map<String, Object> response = new HashMap<>();
        
        Utilisateur utilisateur = getUtilisateur(token);
        if (utilisateur == null) {
            response.put("success", false);
            response.put("message", "Utilisateur non trouvé");
            return ResponseEntity.badRequest().body(response);
        }
        
        Commande commande = commandeRepository.findById(id).orElse(null);
        if (commande == null) {
            response.put("success", false);
            response.put("message", "Commande non trouvée");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (!commande.getUtilisateur().getId().equals(utilisateur.getId())) {
            response.put("success", false);
            response.put("message", "Vous n'êtes pas autorisé");
            return ResponseEntity.status(403).body(response);
        }
        
        if (!"EN_ATTENTE".equals(commande.getStatut())) {
            response.put("success", false);
            response.put("message", "Seules les commandes en attente peuvent être payées");
            return ResponseEntity.badRequest().body(response);
        }
        
        String operateur = request.get("modePaiement");
        String numeroTelephone = request.get("numeroTelephone");
        String reference = request.get("reference");
        
        if (operateur == null || (!"MTN".equals(operateur) && !"ORANGE".equals(operateur))) {
            response.put("success", false);
            response.put("message", "Opérateur invalide");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (numeroTelephone == null || numeroTelephone.length() < 9) {
            response.put("success", false);
            response.put("message", "Numéro de téléphone invalide");
            return ResponseEntity.badRequest().body(response);
        }
        
        Paiement paiement = new Paiement();
        paiement.setCommande(commande);
        paiement.setReferencePaiement(reference);
        paiement.setMontant(commande.getMontantTotal());
        paiement.setOperateur(operateur);
        paiement.setNumeroTelephone(numeroTelephone);
        paiement.setStatut("PENDING");
        paiementRepository.save(paiement);
        
        response.put("success", true);
        response.put("paiementId", paiement.getId());
        response.put("reference", paiement.getReferencePaiement());
        response.put("message", "Paiement initialisé");
        
        return ResponseEntity.ok(response);
    }

    // ✅ Mettre à jour le statut d'une commande (client)
@PutMapping("/{id}/statut")
public ResponseEntity<Map<String, Object>> updateStatutClient(
        @PathVariable Long id,
        @RequestBody Map<String, String> request,
        @RequestHeader("Authorization") String token) {
    
    Map<String, Object> response = new HashMap<>();
    
    try {
        // 1. Vérifier l'utilisateur
        Utilisateur utilisateur = getUtilisateur(token);
        if (utilisateur == null) {
            response.put("success", false);
            response.put("message", "Utilisateur non trouvé");
            return ResponseEntity.badRequest().body(response);
        }
        
        // 2. Vérifier la commande
        Commande commande = commandeRepository.findById(id).orElse(null);
        if (commande == null) {
            response.put("success", false);
            response.put("message", "Commande non trouvée");
            return ResponseEntity.badRequest().body(response);
        }
        
        // 3. Vérifier que la commande appartient à l'utilisateur
        if (!commande.getUtilisateur().getId().equals(utilisateur.getId())) {
            response.put("success", false);
            response.put("message", "Vous n'êtes pas autorisé à modifier cette commande");
            return ResponseEntity.status(403).body(response);
        }
        
        // 4. Récupérer le nouveau statut
        String nouveauStatut = request.get("statut");
        
        // 5. Vérifier que le statut est valide
        String[] statutsValides = {"PAYEE", "ANNULEE"};
        boolean valide = false;
        for (String s : statutsValides) {
            if (s.equals(nouveauStatut)) {
                valide = true;
                break;
            }
        }
        
        if (!valide) {
            response.put("success", false);
            response.put("message", "Statut invalide pour un client");
            return ResponseEntity.badRequest().body(response);
        }
        
        // 6. Vérifier que la commande est en attente
        if (!"EN_ATTENTE".equals(commande.getStatut())) {
            response.put("success", false);
            response.put("message", "Seules les commandes en attente peuvent être modifiées");
            return ResponseEntity.badRequest().body(response);
        }
        
        // 7. Mettre à jour le statut
        commande.setStatut(nouveauStatut);
        commandeRepository.save(commande);
        
        System.out.println("📝 Commande " + id + " : " + "EN_ATTENTE" + " → " + nouveauStatut);
        
        response.put("success", true);
        response.put("message", "Statut mis à jour avec succès");
        response.put("nouveauStatut", nouveauStatut);
        
    } catch (Exception e) {
        e.printStackTrace();
        response.put("success", false);
        response.put("message", "Erreur: " + e.getMessage());
    }
    
    return ResponseEntity.ok(response);
}
    
    // Confirmer le paiement (webhook)
    @PostMapping("/paiement/confirmer")
    public ResponseEntity<Map<String, Object>> confirmerPaiement(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        String reference = request.get("reference");
        String transactionId = request.get("transactionId");
        
        Paiement paiement = paiementRepository.findByReferencePaiement(reference).orElse(null);
        if (paiement == null) {
            response.put("success", false);
            response.put("message", "Paiement non trouvé");
            return ResponseEntity.badRequest().body(response);
        }
        
        paiement.setStatut("SUCCESS");
        paiement.setTransactionId(transactionId);
        paiement.setDateConfirmation(LocalDateTime.now());
        paiementRepository.save(paiement);
        
        Commande commande = paiement.getCommande();
        commande.setStatut("PAYEE");
        commandeRepository.save(commande);

        if("PAYEE".equals(commande.getStatut())){
            //NOTIFICATION au client
            Map<String, Object> data = new HashMap<>();
            data.put("commandeId", commande.getId());
            data.put("montant", commande.getMontantTotal());
    
            notificationService.creerNotification(
                commande.getUtilisateur(),
                "Paiement effectué",
                "Vous venez d'effectuer le paiement de la commande #" + commande.getId(),
                "PAIEMENT_EFFECTUE",
                data
            );

                //NOTIFICATION aux admins
            notificationService.notifierTousLesAdmins(
                "Nouvelle commande !",
                "Commande #" + commande.getId() + " à préparer !",
                "NOUVELLE_COMMANDE",
                data
            );
        }
        
        response.put("success", true);
        response.put("message", "Paiement confirmé");
        
        return ResponseEntity.ok(response);
    }
}