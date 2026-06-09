//Définir comment est enregistrer un utilisateur

package com.ecommerce.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "utilisateurs")
public class Utilisateur {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String firebaseUid;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    private String prenom;
    private String nom;
    private String adresse;
    private String telephone;
    private String ville;
    private String codePostal;
    private String pays;
    private String role = "CLIENT";
  //  private boolean emailVerifie;
  //  private boolean actif = true;
    private LocalDateTime dateInscription;
    
    @PrePersist
    protected void onCreate() {
        dateInscription = LocalDateTime.now();
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFirebaseUid() { return firebaseUid; }
    public void setFirebaseUid(String firebaseUid) { this.firebaseUid = firebaseUid; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
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
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    // public boolean isEmailVerifie() { return emailVerifie; }
    // public void setEmailVerifie(boolean emailVerifie) { this.emailVerifie = emailVerifie; }
    // public boolean isActif() { return actif; }
    // public void setActif(boolean actif) { this.actif = actif; }
    public LocalDateTime getDateInscription() { return dateInscription; }
    public void setDateInscription(LocalDateTime dateInscription) { this.dateInscription = dateInscription; }
}