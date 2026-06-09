//Coordonne le travail , sert de chef d'orchestre

package com.ecommerce.service;

import com.ecommerce.dto.InscriptionRequest;
import com.ecommerce.dto.LoginRequest;
import com.ecommerce.model.Utilisateur;
import com.ecommerce.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class UtilisateurService {
    
    private final UtilisateurRepository utilisateurRepository;
    private final FirebaseAuthService firebaseAuthService;
    
    public UtilisateurService(UtilisateurRepository utilisateurRepository, 
                              FirebaseAuthService firebaseAuthService) {
        this.utilisateurRepository = utilisateurRepository;
        this.firebaseAuthService = firebaseAuthService;
    }
    
    public Map<String, Object> inscription(InscriptionRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            //Vérifie l'unicité de l'email
            if (utilisateurRepository.existsByEmail(request.getEmail())) {
                response.put("success", false);
                response.put("message", "Cet email est déjà utilisé");
                return response;
            }
            //vérifie si le frontend a bien envoyer l'uid
            if (request.getFirebaseToken() == null || request.getFirebaseToken().isEmpty()) {
                response.put("success", false);
                response.put("message", "Token Firebase manquant");
                return response;
            }
            //Récupération des informations de l'utilisateur sur la requête et enregistrement 
            var decodedToken = firebaseAuthService.verifierToken(request.getFirebaseToken());
            
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setFirebaseUid(decodedToken.getUid());
            utilisateur.setEmail(request.getEmail());
            utilisateur.setPrenom(request.getPrenom());
            utilisateur.setNom(request.getNom());
            utilisateur.setAdresse(request.getAdresse());
            utilisateur.setTelephone(request.getTelephone());
            utilisateur.setVille(request.getVille());
            utilisateur.setCodePostal(request.getCodePostal());
            utilisateur.setPays(request.getPays());
            
            //long nbUtilisateurs = utilisateurRepository.count();
            utilisateur.setRole("CLIENT");
            
            utilisateurRepository.save(utilisateur);
            
            response.put("success", true);
            response.put("message", "Inscription réussie !");
            response.put("role", utilisateur.getRole());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    public Map<String, Object> connexion(LoginRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            //On recupère le token envoyer par le frontend puis on recupere l'uid a firebase
            var decodedToken = firebaseAuthService.verifierToken(request.getFirebaseToken());
            String uid = decodedToken.getUid();
            //On vérifie si l'utilisateur existe bien dans la BD
            Utilisateur utilisateur = utilisateurRepository.findByFirebaseUid(uid)
                .orElse(null);
            
            //On vérifie si l'utilisateur est non trouvé
            if (utilisateur == null) {
                response.put("success", false);
                response.put("message", "Utilisateur non trouvé");
                return response;
            }
            
            response.put("success", true);
            response.put("message", "Connexion réussie !");
            response.put("role", utilisateur.getRole());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Token invalide");
        }
        return response;
    }
}