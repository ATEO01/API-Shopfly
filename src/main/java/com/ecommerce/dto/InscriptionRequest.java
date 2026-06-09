//c'est le formulaire que le fontend envoie au backend

package com.ecommerce.dto;

public class InscriptionRequest {
    private String email;
    private String motDePasse;
    private String prenom;
    private String nom;
    private String firebaseToken;
    private String adresse;
    private String telephone;
    private String ville;
    private String codePostal;
    private String pays;
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getFirebaseToken() { return firebaseToken; }
    public void setFirebaseToken(String firebaseToken) { this.firebaseToken = firebaseToken; }
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }
    public String getCodePostal() { return codePostal; }
    public void setCodePostal(String codePostal) { this.codePostal = codePostal; }
    public String getPays() { return pays; }
    public void setPays(String pays) { this.pays = pays; }
}