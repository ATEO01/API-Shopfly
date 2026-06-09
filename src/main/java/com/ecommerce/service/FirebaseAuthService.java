//interprète qui parle avec firebase; il sert de traducteur

package com.ecommerce.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j // sert a loguer cad afficher les erreurs en cas d'erreur et les réussites en cas de réussites 
public class FirebaseAuthService {
    
    //  Créer un utilisateur dans Firebase (INSCRIPTION)
    // public UserRecord creerUtilisateurFirebase(String email, String motDePasse, 
    //                                             String prenom, String nom) {
    //     try {
    //         UserRecord.CreateRequest request = new UserRecord.CreateRequest()
    //             .setEmail(email)
    //             .setPassword(motDePasse)
    //             .setDisplayName(prenom + " " + nom)
    //             .setEmailVerified(false);  // Email non vérifié par défaut
            
    //         UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
    //         System.out.println("✅ Utilisateur Firebase créé: " + email);
    //         return userRecord;
    //     } catch (Exception e) {
    //         System.err.println("❌ Erreur création Firebase: " + e.getMessage());
    //         throw new RuntimeException("Impossible de créer l'utilisateur: " + e.getMessage());
    //     }
    // }
    
    // Vérifier un token Firebase (CONNEXION)
    public FirebaseToken verifierToken(String token) {
        try {
            return FirebaseAuth.getInstance().verifyIdToken(token);
        } catch (Exception e) {
            System.err.println("❌ Token invalide: " + e.getMessage());
            // log.err("Erreur:");
            throw new RuntimeException("Token invalide: " + e.getMessage());
            // L'exécution du programme s'arrête après le throw donc elle sert ici à abandonner
        }
    }
    
    // Récupérer un utilisateur Firebase par UID
    public UserRecord getUtilisateurFirebase(String uid) {
        try {
            return FirebaseAuth.getInstance().getUser(uid);
        } catch (Exception e) {
            return null;
        }
    }
    
    // Récupérer un utilisateur par email
    public UserRecord getUtilisateurParEmail(String email) {
        try {
            return FirebaseAuth.getInstance().getUserByEmail(email);
        } catch (Exception e) {
            return null;
        }
    }
}