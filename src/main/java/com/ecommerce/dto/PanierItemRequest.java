package com.ecommerce.dto;

public class PanierItemRequest {
    private Long produitId;
    private Integer quantite;
    
    // Getters et Setters
    public Long getProduitId() { return produitId; }
    public void setProduitId(Long produitId) { this.produitId = produitId; }
    public Integer getQuantite() { return quantite; }
    public void setQuantite(Integer quantite) { this.quantite = quantite; }
}