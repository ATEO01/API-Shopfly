package com.ecommerce.service;

import com.ecommerce.model.Utilisateur;
import com.ecommerce.repository.UtilisateurRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
public class InitService implements CommandLineRunner { //execute au demarrage grace a commandLineRunner
    
    private final UtilisateurRepository utilisateurRepository;
    
    public InitService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }
    
    @Override
    public void run(String... args) throws Exception {

        String Email = "qdb@gmail.com";
        String Password = "Qdb12345";
        String nom = "qdb";
        String Prenom = "Admin";

        // Vérifier si l'admin existe déjà
        boolean adminExiste = utilisateurRepository.findByEmail("Email").isPresent();
        
        if (!adminExiste) {
            System.out.println("👑 Création de l'administrateur principal");
            
            try {
                // Créer l'utilisateur dans Firebase Auth
                UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(Email)
                    .setPassword(Password)  
                    .setDisplayName("Admin Qdb")
                    .setEmailVerified(true);  
                
                UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
                String uid = userRecord.getUid();
                
                System.out.println(" Utilisateur Firebase créé avec UID: " + uid);
                
                // Créer l'utilisateur dans PostgreSQL
                Utilisateur admin = new Utilisateur();
                admin.setFirebaseUid(uid);
                admin.setEmail(Email);
                admin.setPrenom(Prenom);
                admin.setNom(nom);
                admin.setRole("ADMIN_PRINCIPAL");
                
                utilisateurRepository.save(admin);
                
            } catch (Exception e) {
                System.err.println(" Erreur création admin Firebase: " + e.getMessage());
                System.err.println(" Vérifiez que votre clé API Firebase est correcte");
            }
        } else {
            System.out.println("👑 Admin principal déjà créer!!!");
        }
    }
}