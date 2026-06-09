package com.ecommerce.model;

import jakarta.persistence.*;

@Entity
@Table(name = "panier_items")
public class PanierItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "panier_id")
    private Panier panier;
    
    @ManyToOne
    @JoinColumn(name = "produit_id")
    private Produit produit;
    
    private Integer quantite;
    private Double prix;
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Panier getPanier() { return panier; }
    public void setPanier(Panier panier) { this.panier = panier; }
    public Produit getProduit() { return produit; }
    public void setProduit(Produit produit) { this.produit = produit; }
    public Integer getQuantite() { return quantite; }
    public void setQuantite(Integer quantite) { this.quantite = quantite; }
    public Double getPrix() { return prix; }
    public void setPrix(Double prix) { this.prix = prix; }
}