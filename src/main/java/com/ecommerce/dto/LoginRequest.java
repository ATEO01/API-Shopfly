//c'est le formulaire que le frontend envoie au backend

package com.ecommerce.dto;

public class LoginRequest {
    private String email;
    private String motDePasse;
    private String firebaseToken;
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
    public String getFirebaseToken() { return firebaseToken; }
    public void setFirebaseToken(String firebaseToken) { this.firebaseToken = firebaseToken; }
}