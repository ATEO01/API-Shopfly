package com.ecommerce.dto;

import java.util.List;

public class PanierResponse {
    private Long id;
    private List<PanierItemDto> items;
    private Double total;
    private Integer nombreArticles;
    private boolean success;
    private String message;
    
    public static class PanierItemDto {
        private Long id;
        private Long produitId;
        private String produitNom;
        private Double prix;
        private Integer quantite;
        private Double sousTotal;
        
        // Getters et Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getProduitId() { return produitId; }
        public void setProduitId(Long produitId) { this.produitId = produitId; }
        public String getProduitNom() { return produitNom; }
        public void setProduitNom(String produitNom) { this.produitNom = produitNom; }
        public Double getPrix() { return prix; }
        public void setPrix(Double prix) { this.prix = prix; }
        public Integer getQuantite() { return quantite; }
        public void setQuantite(Integer quantite) { this.quantite = quantite; }
        public Double getSousTotal() { return sousTotal; }
        public void setSousTotal(Double sousTotal) { this.sousTotal = sousTotal; }
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public List<PanierItemDto> getItems() { return items; }
    public void setItems(List<PanierItemDto> items) { this.items = items; }
    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }
    public Integer getNombreArticles() { return nombreArticles; }
    public void setNombreArticles(Integer nombreArticles) { this.nombreArticles = nombreArticles; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}