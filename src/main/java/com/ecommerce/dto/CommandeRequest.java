package com.ecommerce.dto;

public class CommandeRequest {
    private String adresseLivraison;
    
    // Constructeurs
    public CommandeRequest() {}
    
    public CommandeRequest(String adresseLivraison) {
        this.adresseLivraison = adresseLivraison;
    }
    
    // Getter et Setter
    public String getAdresseLivraison() {
        return adresseLivraison;
    }
    
    public void setAdresseLivraison(String adresseLivraison) {
        this.adresseLivraison = adresseLivraison;
    }
}