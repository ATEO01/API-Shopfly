package com.ecommerce.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "commandes")
public class Commande {
    
    // Constantes de statut
    public static final String STATUT_EN_ATTENTE = "EN_ATTENTE";
    public static final String STATUT_EN_COURS_DE_PREPARATION = "EN_COURS_DE_PREPARATION";
    public static final String STATUT_PRET = "PRET";
    public static final String STATUT_EN_COURS_DE_LIVRAISON = "EN_COURS_DE_LIVRAISON";
    public static final String STATUT_LIVRE = "LIVRE";
    public static final String STATUT_ANNULEE = "ANNULEE";
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;
    
    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommandeItem> items = new ArrayList<>();
    
    private Double montantTotal;
    private String statut;
    private LocalDateTime dateCommande;
    private String adresseLivraison;
    
    @ManyToOne
    @JoinColumn(name = "livraison_id", nullable = true)
    private Livraison livraison;
    
    @PrePersist
    protected void onCreate() {
        dateCommande = LocalDateTime.now();
        if (statut == null) statut = STATUT_EN_ATTENTE;
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }
    
    public List<CommandeItem> getItems() { return items; }
    public void setItems(List<CommandeItem> items) { this.items = items; }
    
    public Double getMontantTotal() { return montantTotal; }
    public void setMontantTotal(Double montantTotal) { this.montantTotal = montantTotal; }
    
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    
    public LocalDateTime getDateCommande() { return dateCommande; }
    public void setDateCommande(LocalDateTime dateCommande) { this.dateCommande = dateCommande; }
    
    public String getAdresseLivraison() { return adresseLivraison; }
    public void setAdresseLivraison(String adresseLivraison) { this.adresseLivraison = adresseLivraison; }
    
    public Livraison getLivraison() { return livraison; }
    public void setLivraison(Livraison livraison) { this.livraison = livraison; }
}