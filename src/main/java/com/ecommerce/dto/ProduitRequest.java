package com.ecommerce.dto;

public class ProduitRequest {
    private String nom;
    private String description;
    private Double prix;
    private String imageUrl;
    private String categorie;
    private String unite;
    private Integer stock;
    
    // Getters et Setters
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getPrix() { return prix; }
    public void setPrix(Double prix) { this.prix = prix; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }
    public String getUnite() { return unite; }
    public void setUnite(String unite) { this.unite = unite; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
}