package com.ecommerce.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "livraisons")
public class Livraison {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String reference;
    
    private String statut;
    
    private LocalDateTime dateCreation;
    private LocalDateTime dateTerminaison;
    
    @OneToMany(mappedBy = "livraison", cascade = CascadeType.ALL)
    private List<Commande> commandes = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        if (statut == null) statut = "PAS_TERMINEE";
    }
    
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getReference() {
        return reference;
    }
    
    public void setReference(String reference) {
        this.reference = reference;
    }
    
    public String getStatut() {
        return statut;
    }
    
    public void setStatut(String statut) {
        this.statut = statut;
    }
    
    public LocalDateTime getDateCreation() {
        return dateCreation;
    }
    
    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
    
    public LocalDateTime getDateTerminaison() {
        return dateTerminaison;
    }
    
    public void setDateTerminaison(LocalDateTime dateTerminaison) {
        this.dateTerminaison = dateTerminaison;
    }
    
    public List<Commande> getCommandes() {
        return commandes;
    }
    
    public void setCommandes(List<Commande> commandes) {
        this.commandes = commandes;
    }
}